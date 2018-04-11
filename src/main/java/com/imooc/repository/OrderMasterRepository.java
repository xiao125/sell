package com.imooc.repository;

import com.imooc.dataobject.OrderMaster;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 订单
 */
public interface OrderMasterRepository extends JpaRepository<OrderMaster,String> {

    //根据openid分页查询
    Page<OrderMaster> findByBuyerOpenid(String buyerOpenid, Pageable pageable);

}
