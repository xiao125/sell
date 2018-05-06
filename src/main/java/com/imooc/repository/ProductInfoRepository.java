package com.imooc.repository;

import com.imooc.dataobject.ProductInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * jpa （实体类与数据库相关联） 继承JpaRepository，
 * 第一个参数：实体类 ，
 * 第二个参数：主键类型（商品信息表主键是商品id，类型是string）
 */
public interface ProductInfoRepository extends JpaRepository<ProductInfo,String> {

    //根据商品状态查询商品列表
    List<ProductInfo> findByProductStatus(Integer productStatus);

}
