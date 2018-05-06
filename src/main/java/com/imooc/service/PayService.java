package com.imooc.service;

import com.imooc.dto.OrderDTO;
import com.lly835.bestpay.model.PayResponse;
import com.lly835.bestpay.model.RefundRequest;
import com.lly835.bestpay.model.RefundResponse;

/**
 * 支付
 *  支付sdk接口相关信息
 */
public interface PayService {



    //微信发起支付
    PayResponse create(OrderDTO orderDTO);


    //微信异步通知
    PayResponse notify(String notifyData);

    //退款
    RefundResponse refund(OrderDTO orderDTO);



}
