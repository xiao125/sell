package com.imooc.service;

import com.imooc.dataobject.ProductInfo;
import com.imooc.dto.CartDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 商品
 */
public interface ProductService {


    //根据商品id查询商品
    ProductInfo findOne(String productId);

    /**
     * 查询所有在架商品列表
     * @param
     * @return
     */
    List<ProductInfo> findUpAll();

    //分页查询
    Page<ProductInfo> findAll(Pageable pageable);

    //更新商品
    ProductInfo save(ProductInfo productInfo);


    //加库存
    void increaseStock(List<CartDTO> cartDTOList);

    //减库存
    void decreaseStock(List<CartDTO> cartDTOList);

    //上架
    ProductInfo onSale(String productId);

    //下架
    ProductInfo offSale(String productId);

}
