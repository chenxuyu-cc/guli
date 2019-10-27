package com.guli.ucenter.controller.api;

import com.google.gson.Gson;
import com.guli.common.constants.ResultCodeEnum;
import com.guli.common.exception.GuliException;
import com.guli.common.util.ExceptionUtils;
import com.guli.common.vo.R;
import com.guli.ucenter.entity.Member;
import com.guli.ucenter.service.MemberService;
import com.guli.ucenter.util.ConstantPropertiesUtil;
import com.guli.ucenter.util.HttpClientUtils;
import com.guli.ucenter.util.JwtUtils;
import com.guli.ucenter.vo.LoginInfoVo;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.mapping.ResultMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

@CrossOrigin
@Controller
@RequestMapping("/api/ucenter/wx")
@Slf4j
public class WxApiController {

    @Autowired
    private MemberService memberService;

    @GetMapping("login")
    public String genQrConnect(HttpSession session){

        System.out.println("sessionId = " + session.getId());

        //微信开放平台授权baseUrl
        String baseUrl = "https://open.weixin.qq.com/connect/qrconnect" +
                "?appid=%s" +
                "&redirect_uri=%s" +
                "&response_type=code" +
                "&scope=snsapi_login" +
                "&state=%s" +
                "#wechat_redirect";

        //回调地址
        String redirectUrl = ConstantPropertiesUtil.WX_OPEN_REDIRECT_URL;
        //获取业务服务器重定向地址
        try {
            redirectUrl = URLEncoder.encode(redirectUrl, "UTF-8"); //url编码
        } catch (UnsupportedEncodingException e) {
            throw new GuliException(ResultCodeEnum.URL_ENCODE_ERROR);
        }

        // 防止csrf攻击（跨站请求伪造攻击）
        //String state = UUID.randomUUID().toString().replaceAll("-", "");//一般情况下会使用一个随机数
        String state = "guli191022";//为了让大家能够使用微信回调跳转服务器，这里填写你在ngrok的前置域名
        System.out.println("生成 state = " + state);
        session.setAttribute("wx-open-state", state);

        //生成qrcodeUrl
        String qrcodeUrl = String.format(
                baseUrl,
                ConstantPropertiesUtil.WX_OPEN_APP_ID,
                redirectUrl,
                state);

        return "redirect:" + qrcodeUrl;
    }


    @GetMapping("callback")
    public String callback(String code, String state, HttpSession session){
        System.out.println("callback......");

        //授权临时票据
        System.out.println("code = " + code);
        System.out.println("state = " + state);

        String stateSession = (String)session.getAttribute("wx-open-state");
        System.out.println("state in session = " + stateSession);
        if (StringUtils.isEmpty(state) || !state.equals(stateSession)){
            log.error("非法的回调请求");
            throw new  GuliException(ResultCodeEnum.ILLEGAL_CALLBACK_REQUEST_ERROR);
        }

        //通过code换取access_token
        String baseAccessTokenUrl = "https://api.weixin.qq.com/sns/oauth2/access_token" +
                "?appid=%s" +
                "&secret=%s" +
                "&code=%s" +
                "&grant_type=authorization_code";

        String accessTokenUrl = String.format(
                baseAccessTokenUrl,
                ConstantPropertiesUtil.WX_OPEN_APP_ID,
                ConstantPropertiesUtil.WX_OPEN_APP_SECRET,
                code);

        String result = null;
        try {
            result = HttpClientUtils.get(accessTokenUrl);
        } catch (Exception e) {
            log.error(ExceptionUtils.getMessage(e));
            throw new GuliException(ResultCodeEnum.FETCH_ACCESSTOKEN_FAILD);

        }

        System.out.println(result);

        Gson gson = new Gson();
        HashMap<String, String> resultMap = gson.fromJson(result, HashMap.class);
        if(resultMap.get("errcode") != null){
            log.error("获取access_token失败" + "，message：" + resultMap.get("errmsg"));
            throw new GuliException(ResultCodeEnum.FETCH_ACCESSTOKEN_FAILD);
        }

        String accessToken = resultMap.get("access_token");
        String openid = resultMap.get("openid");
        System.out.println(accessToken);
        System.out.println(openid);

        //根据openid查询当前用户是否已经使用微信登录过该系统
        Member member = memberService.getByOpenid(openid);

        if(member == null){
            //向微信的资源服务器发起请求，获取当前用户的用户信息
            String baseUserInfoUrl = "https://api.weixin.qq.com/sns/userinfo" +
                    "?access_token=%s" +
                    "&openid=%s";

            String userInfoUrl = String.format(
                    baseUserInfoUrl,
                    accessToken,
                    openid);

            String resultUserInfo = null;
            try {
                resultUserInfo = HttpClientUtils.get(userInfoUrl);
            } catch (Exception e) {
                log.error(ExceptionUtils.getMessage(e));
                throw new GuliException(ResultCodeEnum.FETCH_USERINFO_ERROR);
            }


            HashMap<String, Object> resultUserInfoMap = gson.fromJson(resultUserInfo, HashMap.class);
            if(resultUserInfoMap.get("errcode") != null){
                log.error("获取用户信息失败" + "，message：" + resultMap.get("errmsg"));
                throw new GuliException(ResultCodeEnum.FETCH_USERINFO_ERROR);
            }

            String nickname = (String)resultUserInfoMap.get("nickname");
            String headimgurl = (String)resultUserInfoMap.get("headimgurl");

            member = new Member();
            member.setOpenid(openid);
            member.setNickname(nickname);
            member.setAvatar(headimgurl);
            memberService.save(member);
        }

        // 登录
        // 生成jwt
        String guliJwtToken = JwtUtils.generateJWT(member);
        return "redirect:http://localhost:3000?token=" + guliJwtToken;
    }


    @PostMapping("parse-jwt")
    @ResponseBody
    public R getLoginInfoBtJwtToken(@RequestBody String jwtToken){


            Claims claims = JwtUtils.checkJwt(jwtToken);
            String id = (String)claims.get("id");
            String nickname = (String)claims.get("nickname");
            String avatar = (String)claims.get("avatar");

            LoginInfoVo loginInfoVo = new LoginInfoVo();
            loginInfoVo.setId(id);
            loginInfoVo.setAvatar(avatar);
            loginInfoVo.setNickname(nickname);

            return R.ok().data("loginInfo", loginInfoVo);


    }
}
