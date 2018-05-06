package com.imooc.dataobject.mapper;

import com.imooc.dataobject.ProductCategory;

public interface ProductCategoryMapper {

    ProductCategory selectByCategoryType(Integer categoryType);

}
