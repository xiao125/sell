package com.imooc.service.impl;

import com.imooc.converter.OrderMaster2OrderDTOConverter;
import com.imooc.dataobject.OrderDetail;
import com.imooc.dataobject.OrderMaster;
import com.imooc.dataobject.ProductInfo;
import com.imooc.dto.CartDTO;
import com.imooc.dto.OrderDTO;
import com.imooc.enums.OrderStatusEnum;
import com.imooc.enums.PayStatusEnum;
import com.imooc.enums.ResultEnum;
import com.imooc.exception.SellException;
import com.imooc.repository.OrderDetailRepository;
import com.imooc.repository.OrderMasterRepository;
import com.imooc.service.*;
import com.imooc.utils.KeyUtil;
import javassist.tools.web.Webserver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {


    @Autowired
    private ProductService productService; //商品信息

    @Autowired
    private OrderDetailRepository orderDetailRepository; //订单中的商品信息

    @Autowired
    private OrderMasterRepository orderMasterRepository; //订单总信息

    @Autowired
    private PayService payService;

    @Autowired
    private PushMessageService pushMessageService;


    @Autowired
    private WebSocket webSocket;

    @Override
    @Transactional //事务管理，一旦失败就回滚
    public OrderDTO create(OrderDTO orderDTO) {
        //设置下订单id(是个随机，这里调用了根据时间产生6位随机数的方法)
        String orderId = KeyUtil.genUniqueKey();
        //给总价赋值 （BigInteger.ZERO 为小数的保留模式，与0比较）
        BigDecimal orderAmount = new BigDecimal(BigInteger.ZERO);

        //1.查询商品（数量，价格）
        for (OrderDetail orderDetail : orderDTO.getOrderDetailList()){

            //根据商品id查询商品信息
            ProductInfo productInfo = productService.findOne(orderDetail.getProductId());
            if (productInfo ==null){
                throw new SellException(ResultEnum.PRODUCT_NOT_EXIST);
            }

        //2.计算订单总价 (数量 * 单价)
        orderAmount = productInfo.getProductPrice()
                    .multiply(new BigDecimal(orderDetail.getProductQuantity()))
                    .add(orderAmount);

            //订单详情入库
            //利用BeanUtils方法把前端查找出来的productInfo商品信息复制给订单详情
            orderDetail.setDetailId(KeyUtil.genUniqueKey());
            orderDetail.setOrderId(orderId);
            BeanUtils.copyProperties(productInfo,orderDetail);
            orderDetailRepository.save(orderDetail);
        }

        //3.写入订单数据库（orderMaster和orderDetail）
        OrderMaster orderMaster = new OrderMaster();
        orderDTO.setOrderId(orderId); //保存orderDTO的orderId
        BeanUtils.copyProperties(orderDTO,orderMaster);
        orderMaster.setOrderAmount(orderAmount); //是一个整个订单的总价，所以在foe循环之外设置
        orderMaster.setOrderStatus(OrderStatusEnum.NEW.getCode());
        orderMaster.setPayStatus(PayStatusEnum.WAIT.getCode());
        orderMasterRepository.save(orderMaster);

        //4.扣库存
        List<CartDTO> cartDTOList = orderDTO.getOrderDetailList().stream()
                             .map(e->new CartDTO(e.getProductId(),e.getProductQuantity())
                             ).collect(Collectors.toList());

        //减库存
        productService.decreaseStock(cartDTOList);

         //发送websocket消息
         webSocket.sendMessage(orderDTO.getOrderId());


        return orderDTO;
    }

    @Override
    public OrderDTO findOne(String orderId) {

        OrderMaster orderMaster = orderMasterRepository.findOne(orderId);
        if (orderMaster == null){ //订单不存在
            throw new SellException(ResultEnum.ORDER_NOT_EXIST);
        }

        //根据订单id查询出商品信息列表
        List<OrderDetail> orderDetailList = orderDetailRepository.findByOrderId(orderId);

        if (CollectionUtils.isEmpty(orderDetailList)){ //订单详情不存在
           throw new SellException(ResultEnum.ORDERDETAIL_NOT_EXIST);
        }

        OrderDTO orderDTO = new OrderDTO();
        BeanUtils.copyProperties(orderMaster,orderDTO);
        orderDTO.setOrderDetailList(orderDetailList);

        return orderDTO;
    }

    @Override
    public Page<OrderDTO> findList(String buyerOpenid, Pageable pageable) {

        Page<OrderMaster> orderMasterPage = orderMasterRepository.findByBuyerOpenid(buyerOpenid,pageable);

       List<OrderDTO> orderDTOList = OrderMaster2OrderDTOConverter.convert(orderMasterPage.getContent());


        return new PageImpl<OrderDTO>(orderDTOList,pageable,orderMasterPage.getTotalElements());
    }

    @Override
    @Transactional
    public OrderDTO cancel(OrderDTO orderDTO) {

        OrderMaster orderMaster = new OrderMaster();

        //判断订单状态
        if (!orderDTO.getOrderStatus().equals(OrderStatusEnum.NEW.getCode())){

            log.error("【取消订单】订单状态不正确, orderId={}, orderStatus={}", orderDTO.getOrderId(), orderDTO.getOrderStatus());
            throw new SellException(ResultEnum.ORDER_STATUS_ERROR);
        }

        //修改订单状态
        orderDTO.setOrderStatus(OrderStatusEnum.CANCEL.getCode());
        BeanUtils.copyProperties(orderDTO,orderMaster);
        OrderMaster updateResult = orderMasterRepository.save(orderMaster);
        if (updateResult == null){
            log.error("【取消订单】更新失败, orderMaster={}", orderMaster);
            throw new SellException(ResultEnum.ORDER_UPDATE_FAIL);
        }

        //返回库存

        if (CollectionUtils.isEmpty(orderDTO.getOrderDetailList())){
            log.error("【取消订单】订单中无商品详情, orderDTO={}", orderDTO);
            throw new SellException(ResultEnum.ORDER_DETAIL_EMPTY);
        }

        List<CartDTO> cartDTOList = orderDTO.getOrderDetailList().stream()
                .map(e-> new CartDTO(e.getProductId(),e.getProductQuantity()))
                .collect(Collectors.toList());
        productService.increaseStock(cartDTOList);

        //如果已支付, 需要退款
        if (orderDTO.getPayStatus().equals(PayStatusEnum.SUCCESS.getCode())){
            //TODO
            payService.refund(orderDTO);

        }

        return orderDTO;
    }

    @Override
    @Transactional
    public OrderDTO finish(OrderDTO orderDTO) {

        //判断订单状态

        if (!orderDTO.getOrderStatus().equals(OrderStatusEnum.NEW.getCode())){
            log.error("【完结订单】订单状态不正确, orderId={}, orderStatus={}", orderDTO.getOrderId(), orderDTO.getOrderStatus());
            throw new SellException(ResultEnum.ORDER_STATUS_ERROR);
        }


        //修改订单状态
        orderDTO.setOrderStatus(OrderStatusEnum.FINISHED.getCode());
        OrderMaster orderMaster = new OrderMaster();
        BeanUtils.copyProperties(orderDTO,orderMaster);
        OrderMaster updateResult = orderMasterRepository.save(orderMaster);
        if (updateResult ==null){

            log.error("【完结订单】更新失败, orderMaster={}", orderMaster);
            throw new SellException(ResultEnum.ORDER_UPDATE_FAIL);
        }

        //推送微信模板消息
        pushMessageService.orderStatus(orderDTO);

        return orderDTO;
    }

    @Override
    @Transactional
    public OrderDTO paid(OrderDTO orderDTO) {

        //判断订单状态
        if (!orderDTO.getOrderStatus().equals(OrderStatusEnum.NEW.getCode())) {
            log.error("【订单支付完成】订单状态不正确, orderId={}, orderStatus={}", orderDTO.getOrderId(), orderDTO.getOrderStatus());
            throw new SellException(ResultEnum.ORDER_STATUS_ERROR);
        }
        //判断支付状态
        if (!orderDTO.getPayStatus().equals(PayStatusEnum.WAIT.getCode())) {
            log.error("【订单支付完成】订单支付状态不正确, orderDTO={}", orderDTO);
            throw new SellException(ResultEnum.ORDER_PAY_STATUS_ERROR);
        }
        //修改支付状态
        orderDTO.setPayStatus(PayStatusEnum.SUCCESS.getCode());
        OrderMaster orderMaster = new OrderMaster();
        BeanUtils.copyProperties(orderDTO, orderMaster);
        OrderMaster updateResult = orderMasterRepository.save(orderMaster);
        if (updateResult == null) {
            log.error("【订单支付完成】更新失败, orderMaster={}", orderMaster);
            throw new SellException(ResultEnum.ORDER_UPDATE_FAIL);
        }


        return orderDTO;
    }

    //查询全部订单列表
    @Override
    public Page<OrderDTO> findList(Pageable pageable) {

        Page<OrderMaster> orderMasterPage = orderMasterRepository.findAll(pageable);

        //将订单信息OrderMaster转成OrderDTO
        List<OrderDTO> orderDTOList = OrderMaster2OrderDTOConverter.convert(orderMasterPage.getContent());


        return new PageImpl<>(orderDTOList,pageable,orderMasterPage.getTotalElements());
    }
}
