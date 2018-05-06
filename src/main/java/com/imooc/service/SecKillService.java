package com.imooc.service;

public interface SecKillService {

    /**
     * 查询秒杀活动特价商品的信息
     * @param productId
     * @return
     */
    String querySeckillProductInfo(String productId);


    /**
     * 模拟不同用户秒杀同一商品的请求
     * @param productId
     */
    void orderProductMockDiffUser(String productId);

}
