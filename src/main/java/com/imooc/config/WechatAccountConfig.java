package com.imooc.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Component：是一个泛化的概念，仅仅表示一个组件 (Bean) ，可以作用在任何层次。
 * （把普通pojo实例化到spring容器中，相当于配置文件中的<bean id="" class=""/>）
 * 用这个注解注册之后就可以用@Autowired来进行调用了
 * */
@Data
@Component
@ConfigurationProperties(prefix = "wechat")  //这是调用配置文件
public class WechatAccountConfig {

    private String mpAppId;

    private String mpAppSecret;

    /**
     * 商户号
     */
    private String mchId;

    /**
     * 商户秘钥
     */
    private String mchKey;

    /**
     * 商户证书路径
     */
    private String keyPath;


    /**
     * 微信支付异步通知地址
     */
    private String notifyUrl;


}
