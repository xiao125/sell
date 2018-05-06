package com.imooc.controller;

import com.imooc.VO.ProductInfoVO;
import com.imooc.VO.ProductVO;
import com.imooc.VO.ResultVO;
import com.imooc.dataobject.ProductCategory;
import com.imooc.dataobject.ProductInfo;
import com.imooc.service.CategoryService;
import com.imooc.service.ProductService;
import com.imooc.utils.ResultVOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 商品信息api接口类
 */

@RestController
@RequestMapping("/buyer/product")
public class BuyerProductController {

    private Logger logger = LoggerFactory.getLogger(BuyerProductController.class);

    @Autowired
    private ProductService productService; //商品

    @Autowired
    private CategoryService categoryService; //商品类目

    @GetMapping("/list")
    /**
     *  @Cacheable:
     *  可以标记在一个方法上，也可以标记在一个类上。当标记一个方法表示该方法支持缓存，
     *  将缓存其返回结果（如果有缓存数据将不执行此注解的，直接获取redis缓存数据，值就是方法的返回结果,否则查询数据库）
     *
     *   cacheNames 属性: 表示当前方法的返回值是会被缓存在哪个Cache上的，对应Cache的名称
     *   key 属性:  SPEL表达式可以使用方法参数及它们对应的属性。使用方法参数时我们可以直接使用“#参数名”或者“#p参数index”
     *   confition 属性 : 指定发生的条件，对传入的参数进行筛选
     *   unless 属性 ：指定发生的条件，返回值进行筛选，当条件为true时，不保存对象
     */
   // @Cacheable(cacheNames = "product",key = "#sellerId",condition = "#sellerId.length()>3",unless = "#result.getCode() !=0")
    public ResultVO list(){

        //1.查询所有上架的商品

        List<ProductInfo> productInfoList = productService.findUpAll();

        //2. 查询类目(一次性查询)
        List<Integer> categoryTypeList = productInfoList.stream()
                .map(e -> e.getCategoryType()) //类目
                .collect(Collectors.toList());

        //根据商品类目编号查询出类目
         List<ProductCategory> productCategoryList = categoryService.findByCategoryTypeIn(categoryTypeList);

         logger.warn("======="+productCategoryList.size()); //类目数量

         //3.数据拼装
        List<ProductVO> productVOList = new ArrayList<>();
        for (ProductCategory productCategory : productCategoryList){

            ProductVO productVO = new ProductVO();
            productVO.setCategoryType(productCategory.getCategoryType());
            productVO.setCategoryName(productCategory.getCategoryName());

            List<ProductInfoVO> productInfoVOList = new ArrayList<>();
            for (ProductInfo productInfo : productInfoList){

                //如果类目编号与商品类目编号相同
                if (productInfo.getCategoryType().equals(productCategory.getCategoryType())){

                    ProductInfoVO productInfoVO = new ProductInfoVO();
                    BeanUtils.copyProperties(productInfo,productInfoVO); //商品内容直接复制到需要显示的json数据上
                    productInfoVOList.add(productInfoVO);

                }
            }

            productVO.setProductInfoVOList(productInfoVOList); //商品
            productVOList.add(productVO);// data

        }

        return ResultVOUtil.success(productVOList);


    }





}
