package com.imooc.repository;

import com.imooc.dataobject.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
//采用的是JPA的方式操作数据库
public interface OrderDetailRepository extends JpaRepository<OrderDetail,String> {

    /**
     * 一条订单可以对应多个商品
     * @param orderId
     * @return
     */
    List<OrderDetail> findByOrderId(String orderId);


}
