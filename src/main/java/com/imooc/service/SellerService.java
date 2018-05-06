package com.imooc.service;

import com.imooc.dataobject.SellerInfo;

/**
 * 卖家端 ，登录service
 */
public interface SellerService {


    /**
     * 通过openid 登录
     * @param openid
     * @return
     */
    SellerInfo findSellerInfoByOpenid(String openid);
}
