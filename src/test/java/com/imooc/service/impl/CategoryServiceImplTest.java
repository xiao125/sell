package com.imooc.service.impl;

import com.imooc.dataobject.ProductCategory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CategoryServiceImplTest {


    @Autowired
    private CategoryServiceImpl categoryService;


    @Test
    public void findOne() throws Exception{

        ProductCategory productCategory = categoryService.findOne(1);
        Assert.assertEquals(new Integer(1),productCategory.getCategoryId());

    }



    @Test
    public void findAll() throws Exception {

        List<ProductCategory> productCategoryList = categoryService.findAll();
        Assert.assertNotEquals(0,productCategoryList.size());

    }

    @Test
    public void findByCategoryTypeIn() throws Exception {

        List<ProductCategory> productCategoryList = categoryService.findByCategoryTypeIn(Arrays.asList(1,2,3,4));
        Assert.assertNotEquals(0,productCategoryList.size());


        /*select productcat0_.category_id as category1_2_, productcat0_.category_name as category2_2_,
        productcat0_.category_type as category3_2_, productcat0_.create_time as create_t4_2_,
        productcat0_.update_time as update_t5_2_
        from product_category productcat0_ where productcat0_.category_type in (? , ? , ? , ?)  */

    }

    @Test
    public void save() throws Exception{

        ProductCategory productCategory = new ProductCategory("男生专享",10);
        ProductCategory result = categoryService.save(productCategory);
        Assert.assertNotNull(result);

    }
}