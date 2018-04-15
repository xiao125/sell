package com.imooc.controller;

import com.imooc.enums.ResultEnum;
import com.imooc.exception.SellException;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URLEncoder;


/**
 * 微信授权接口
 */
@Controller
@RequestMapping("/wechat")
@Slf4j
public class WechatController {

    @Autowired
    private WxMpService wxMpService;



    /**
     *  接口请求地址：xixi.nat100.top/sell/wechat/authorize?returnUrl=xixi.nat100.top
     *  此接口重定向微信授权接口url：https://open.weixin.qq.com/connect/oauth2/authorize?appid=APPID&redirect_uri=REDIRECT_URI&response_type=code&scope=SCOPE&state=STATE#wechat_redirect
     *
     *  returnUrl : 自己设置的重定向的url ，后面作为微信授权state参数，
     *
     *
     */



    @GetMapping("/authorize")
    public String authorize(@RequestParam("returnUrl") String returnUrl){

        //1.配置
        //2.调用方法
        String url ="http://xixi.nat100.top/sell/wechat/userInfo";
        //这个redirectUrl是微信授权拼接地址
        String redirectUrl = wxMpService.oauth2buildAuthorizationUrl(url, WxConsts.OAUTH2_SCOPE_BASE, URLEncoder.encode(returnUrl));

        log.error("微信授权重定向地址："+redirectUrl);

        return "redirect:"+redirectUrl;
    }


    /**
     *  如果用户同意授权，页面将跳转至 redirect_uri/?code=CODE&state=STATE。
     *  完整url：xixi.nat100.top/sell/wechat/userInfo/?code=CODE&state=STATE
     *  state参数是微信请求授权接口原样返回的 ，然后重定向到我们需要的url（授权成功后我们指定跳转的url页面）
     */
    @GetMapping("/userInfo")
    public String userInfo(@RequestParam("code") String code,
                           @RequestParam("state") String returnUrl){


        WxMpOAuth2AccessToken wxMpOAuth2AccessToken = new WxMpOAuth2AccessToken();

        try{

            wxMpOAuth2AccessToken = wxMpService.oauth2getAccessToken(code);
        }catch (WxErrorException e){
            log.error("【微信网页授权】{}",e);
            throw new SellException(ResultEnum.WECHAT_MP_ERROR.getCode(),e.getError().getErrorMsg());
        }

        String openId = wxMpOAuth2AccessToken.getOpenId();

        log.error("微信授权成功重定向地址："+returnUrl+"?openid="+openId);
        //获取到微信返回的openid后,重定向到 Url地址:   www.imooc.com?openId=XXXXXXXXXXX
        return "redirect:"+returnUrl + "?openid="+openId;
    }




}
