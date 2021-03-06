package com.imooc.service.impl;

import com.imooc.dataobject.ProductInfo;
import com.imooc.dto.CartDTO;
import com.imooc.enums.ProductStatusEnum;
import com.imooc.enums.ResultEnum;
import com.imooc.exception.SellException;
import com.imooc.repository.ProductInfoRepository;
import com.imooc.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
//@CacheConfig(cacheNames = "product")
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductInfoRepository repository;

    @Override
    // @Cacheable(key= "#productId")
    public ProductInfo findOne(String productId) {
        return repository.findOne(productId);
    }

    @Override
    public List<ProductInfo> findUpAll() { //查询上架的全部商品
        return repository.findByProductStatus(ProductStatusEnum.UP.getCode());
    }

    @Override
    public Page<ProductInfo> findAll(Pageable pageable) { //分页查询
        return repository.findAll(pageable);
    }

    @Override
  //  @CachePut(key = "#productInfo.getProductId()")
    /**
     * @CachePut : 标注的方法在执行前不会去检查缓存中是否存在之前执行过的结果，而是每次都会执行该方法，
     * 并将执行结果以键值对的形式存入指定的缓存中
     *
     *
     */
    public ProductInfo save(ProductInfo productInfo) {  //添加商品
        return repository.save(productInfo);
    }

    @Override
    @Transactional
    public void increaseStock(List<CartDTO> cartDTOList) { //增加库存


        for (CartDTO cartDTO : cartDTOList){

            ProductInfo productInfo = repository.findOne(cartDTO.getProductId());
            if (productInfo == null){

                throw new SellException(ResultEnum.PRODUCT_NOT_EXIST);

            }

            //购物车商品的数量+ 商品本身的库存
            Integer result = productInfo.getProductStock() + cartDTO.getProductQuantity();
            productInfo.setProductStock(result);
            repository.save(productInfo);


        }



    }

    @Override
    @Transactional
    public void decreaseStock(List<CartDTO> cartDTOList) { //减少库存

        for (CartDTO cartDTO : cartDTOList){
            //查询出商品
            ProductInfo productInfo = repository.findOne(cartDTO.getProductId());

            if (productInfo == null){

                throw new SellException(ResultEnum.PRODUCT_NOT_EXIST);
            }

            Integer result = productInfo.getProductStock() - cartDTO.getProductQuantity();
            if (result <0){ //商品库存不正确
                throw new SellException(ResultEnum.PRODUCT_STOCK_ERROR);
            }

            //更新商品库存
            productInfo.setProductStock(result);
            repository.save(productInfo);

        }


    }


    //上架
    @Override
    public ProductInfo onSale(String productId) {

        ProductInfo productInfo = repository.findOne(productId);
        if (productId == null){
            throw new SellException(ResultEnum.PRODUCT_NOT_EXIST);
        }
        if (productInfo.getProductStatusEnum() == ProductStatusEnum.UP){
            throw new SellException(ResultEnum.PRODUCT_STATUS_ERROR);
        }

        productInfo.setProductStatus(ProductStatusEnum.UP.getCode());
        return repository.save(productInfo);
    }

    @Override
    public ProductInfo offSale(String productId) {
        ProductInfo productInfo = repository.findOne(productId);
        if (productId == null){
            throw new SellException(ResultEnum.PRODUCT_NOT_EXIST);
        }
        if (productInfo.getProductStatusEnum() == ProductStatusEnum.DOWN){
            throw new SellException(ResultEnum.PRODUCT_STATUS_ERROR);
        }

        //更新
        productInfo.setProductStatus(ProductStatusEnum.DOWN.getCode());
        return repository.save(productInfo);
    }
}
