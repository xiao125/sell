package com.imooc.repository;

import com.imooc.dataobject.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory,Integer> {


    //根据类目编号查询商品类目列表
    List<ProductCategory> findByCategoryTypeIn(List<Integer> categoryTypeList);

}
