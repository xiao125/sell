package com.imooc.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.imooc.dataobject.OrderDetail;
import com.imooc.enums.OrderStatusEnum;
import com.imooc.enums.PayStatusEnum;
import com.imooc.utils.EnumUtil;
import com.imooc.utils.serializer.Date2LongSerializer;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 *  DTO类用来关联dataobject中有联系的类，比如创建订单就需要订单总表和订单详情表两种数据，
 * 所以就需要一种包含了这两种实体的包装类把他们联系起来
 * 因为用dataobject来关联的话会破坏映射的数据库的关系
 */

@Data
//@JsonInclude(JsonInclude.Include.NON_NULL) //不显示为null的字段
public class OrderDTO {

    /** 订单id. */
    private String orderId;

    /** 买家名字. */
    private String buyerName;

    /** 买家手机号. */
    private String buyerPhone;

    /** 买家地址. */
    private String buyerAddress;

    /** 买家微信Openid. */
    private String buyerOpenid;

    /** 订单总金额. */
    private BigDecimal orderAmount;

    /** 订单状态, 默认为0新下单. */
    private Integer orderStatus;

    /** 支付状态, 默认为0未支付. */
    private Integer payStatus;

    /** 创建时间. */

    @JsonSerialize(using = Date2LongSerializer.class)
    private Date createTime;

    /** 更新时间. */
    @JsonSerialize(using = Date2LongSerializer.class)
    private Date updateTime;


    List<OrderDetail> orderDetailList;

    @JsonIgnore // json数据时不显示此参数
    public OrderStatusEnum getOrderStatusEnum(){

        return EnumUtil.getByCode(orderStatus,OrderStatusEnum.class);
    }


    @JsonIgnore
    public PayStatusEnum getPayStatusEnum(){

        return EnumUtil.getByCode(payStatus,PayStatusEnum.class);

    }


}
