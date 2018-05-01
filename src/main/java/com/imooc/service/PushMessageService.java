package com.imooc.service;

import com.imooc.dto.OrderDTO;

/**
 * 微信支付消息推送
 */
public interface PushMessageService {


    /**
     * 订单状态变更消息
     * @param orderDTO
     */
    void orderStatus(OrderDTO orderDTO);


}
