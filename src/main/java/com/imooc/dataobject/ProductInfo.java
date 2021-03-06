package com.imooc.dataobject;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.imooc.enums.ProductStatusEnum;
import com.imooc.utils.EnumUtil;
import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 商品信息
 */
@Entity
@Data
@DynamicUpdate
public class ProductInfo {

    @Id
    private String productId;

    /**
     * 名字
     */
    private String productName;

    /**
     * 单价  BigDecimal:常用于金额的计算
     */
    private BigDecimal productPrice;

    /**
     * 库存
     */
    private Integer productStock;


    /**
     * 描述
     */
    private String productDescription;

    /**
     * 小图
     */
    private String productIcon;

    /**
     * 状态, 0正常1下架.
     */
    private Integer productStatus =ProductStatusEnum.UP.getCode();

    /**
     * 类目编号
     */
    private Integer categoryType;


    private Date createTime;

    private Date updateTime;

    @JsonIgnore //生成json时将此注解属性过滤
    public ProductStatusEnum getProductStatusEnum(){ //获取商品状态，code值，0或1

        return EnumUtil.getByCode(productStatus,ProductStatusEnum.class);
    }

}
