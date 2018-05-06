package com.imooc.VO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 *  返回给前端的VO对象，主要按照前端API开发
 *
 *   VO的区别是DTO包含了整个原始对象，而VO缩减了一些属性
 * 需要返回的商品详情
 */
@Data
public class ProductInfoVO implements Serializable {

    private static final long serialVersionUID = -1066050991974318218L;
    //为了防止多个name造成混淆，所以要细起名，但为了和返回对象名一致，所以用这个注解
    //其实也不是造成混淆，主要原因还是为了和原productId对象中属性名一致并且为了和前端API一致，才要在这里起别名，让他在返回时实例化成别的名字
    @JsonProperty("id")
    private String productId;


    @JsonProperty("name")
    private String productName;

    @JsonProperty("price")
    private String productPrice;


    @JsonProperty("description")
    private String productDescription;

    @JsonProperty("icon")
    private String productIcon;


}
