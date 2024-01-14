package xyz.funnyboy.yygh.user.api;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import xyz.funnyboy.yygh.common.exception.YyghException;
import xyz.funnyboy.yygh.common.helper.JwtHelper;
import xyz.funnyboy.yygh.common.result.Result;
import xyz.funnyboy.yygh.common.result.ResultCodeEnum;
import xyz.funnyboy.yygh.model.user.UserInfo;
import xyz.funnyboy.yygh.user.service.UserInfoService;
import xyz.funnyboy.yygh.user.utils.ConstantWxPropertiesUtils;
import xyz.funnyboy.yygh.user.utils.HttpClientUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 微信登录接口
 *
 * @author VectorX
 * @version V1.0
 * @date 2024-01-13 18:24:28
 */
@Api(tags = "微信登录接口")
@Controller
@RequestMapping("/api/ucenter/wx")
@Slf4j
public class WeixinApiController
{
    @Autowired
    private UserInfoService userInfoService;

    @ApiOperation(value = "生成微信扫描二维码")
    @GetMapping("getLoginParam")
    @ResponseBody
    public Result genQrConnect() {
        log.info("生成微信扫描二维码。。。。。。");
        Map<String, Object> map = new HashMap<>();
        // appid：应用唯一标识，在微信开放平台提交应用审核通过后获得
        map.put("appid", ConstantWxPropertiesUtils.WX_OPEN_APP_ID);
        // scope：应用授权作用域，拥有多个作用域用逗号（,）分隔，网页应用目前仅填写snsapi_login即可
        map.put("scope", "snsapi_login");
        // redirect_uri：重定向地址，需要进行UrlEncode
        // map.put("redirect_uri", URLEncoder.encode(ConstantWxPropertiesUtils.WX_OPEN_REDIRECT_URL, "utf-8"));
        map.put("redirect_uri", ConstantWxPropertiesUtils.WX_OPEN_REDIRECT_URI);
        // state：用于保持请求和回调的状态，授权请求后原样带回给第三方。该参数可用于防止csrf攻击（跨站请求伪造攻击），建议第三方带上该参数，可设置为简单的随机数加session进行校验
        map.put("state", System.currentTimeMillis() + "");
        return Result.ok(map);
    }

    /**
     * 微信扫描后回调的方法
     *
     * @param code  code
     * @param state 状态
     * @return {@link String}
     */
    @GetMapping("callback")
    public String callback(String code, String state) {
        // ================================================第一步：请求CODE================================================
        log.info("微信授权服务器回调。。。。。。");
        log.info("state = {}", state);
        log.info("code = {}", code);
        if (StringUtils.isEmpty(state) || StringUtils.isEmpty(code)) {
            log.error("非法回调请求");
            throw new YyghException(ResultCodeEnum.ILLEGAL_CALLBACK_REQUEST_ERROR);
        }

        // ================================================第二步：通过code获取access_token================================================
        //使用code和appid以及appscrect换取access_token
        StringBuffer baseAccessTokenUrl = new StringBuffer()
                // 通过code获取access_token
                .append("https://api.weixin.qq.com/sns/oauth2/access_token")
                // appid：应用唯一标识，在微信开放平台提交应用审核通过后获得
                .append("?appid=%s")
                // secret：应用密钥AppSecret，在微信开放平台提交应用审核通过后获得
                .append("&secret=%s")
                // code：填写第一步获取的code参数
                .append("&code=%s")
                // grant_type：填authorization_code
                .append("&grant_type=%s");
        String accessTokenUrl = String.format(baseAccessTokenUrl.toString(), ConstantWxPropertiesUtils.WX_OPEN_APP_ID, ConstantWxPropertiesUtils.WX_OPEN_APP_SECRET, code,
                "authorization_code");

        //使用httpclient请求这个地址
        final String result;
        try {
            result = HttpClientUtils.get(accessTokenUrl);
            log.info("使用code换取的access_token结果 = {}", result);
        }
        catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new YyghException(ResultCodeEnum.FETCH_ACCESSTOKEN_FAILD);
        }

        // 从返回字符串获取两个值 openid  和  access_token
        JSONObject resultJson = JSONObject.parseObject(result);
        if (resultJson.getString("errcode") != null) {
            log.error("获取用户信息失败：" + resultJson.getString("errcode") + resultJson.getString("errmsg"));
            throw new YyghException(ResultCodeEnum.FETCH_USERINFO_ERROR);
        }
        final String accessToken = resultJson.getString("access_token");
        final String openId = resultJson.getString("openid");
        log.info("access_token = {}", accessToken);
        log.info("openid = {}", openId);

        // ================================================第三步：通过access_token获取用户个人信息================================================
        //根据access_token获取微信用户的基本信息
        //先根据openid进行数据库查询
        UserInfo userInfo = userInfoService.getByOpenid(openId);
        // 如果没有查到用户信息,那么调用微信个人信息获取的接口
        if (null == userInfo) {
            //如果查询到个人信息，那么直接进行登录
            //使用access_token换取受保护的资源：微信的个人信息
            StringBuilder baseUserInfoUrl = new StringBuilder();
            baseUserInfoUrl
                    .append("https://api.weixin.qq.com/sns/userinfo")
                    .append("?access_token=%s")
                    .append("&openid=%s");
            String userInfoUrl = String.format(baseUserInfoUrl.toString(), accessToken, openId);
            final String resultUserInfo;
            try {
                resultUserInfo = HttpClientUtils.get(userInfoUrl);
            }
            catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new YyghException(ResultCodeEnum.FETCH_USERINFO_ERROR);
            }
            log.info("使用access_token获取用户信息的结果 = {}", resultUserInfo);

            JSONObject resultUserInfoJson = JSONObject.parseObject(resultUserInfo);
            if (resultUserInfoJson.getString("errcode") != null) {
                log.error("获取用户信息失败：{} {}", resultUserInfoJson.getString("errcode"), resultUserInfoJson.getString("errmsg"));
                throw new YyghException(ResultCodeEnum.FETCH_USERINFO_ERROR);
            }

            //解析用户信息
            String nickname = resultUserInfoJson.getString("nickname");
            String headimgurl = resultUserInfoJson.getString("headimgurl");

            //获取扫描人信息添加数据库
            userInfo = new UserInfo();
            userInfo.setOpenid(openId);
            userInfo.setNickName(nickname.replaceAll("[\ue000-\uefff]", ""));
            userInfo.setStatus(1);
            userInfoService.save(userInfo);
        }

        // ================================================第五步：封装跳转页面参数================================================
        //返回name和token字符串
        Map<String, String> map = new HashMap<>();
        String name = userInfo.getName();
        if (StringUtils.isEmpty(name)) {
            name = userInfo.getNickName();
        }
        if (StringUtils.isEmpty(name)) {
            name = userInfo.getPhone();
        }
        map.put("name", name);

        //判断userInfo是否有手机号，如果手机号为空，返回openid
        //如果手机号不为空，返回openid值是空字符串
        //前端判断：如果openid不为空，绑定手机号，如果openid为空，不需要绑定手机号
        if (StringUtils.isEmpty(userInfo.getPhone())) {
            map.put("openid", userInfo.getOpenid());
        }
        else {
            map.put("openid", "");
        }

        //使用jwt生成token字符串
        String token = JwtHelper.createToken(userInfo.getId(), name);
        map.put("token", token);

        //跳转到前端页面
        StringBuilder url = new StringBuilder();
        url
                .append(ConstantWxPropertiesUtils.YYGH_BASE_URL)
                .append("/weixin/callback")
                .append("?token=%s")
                .append("&openid=%s")
                .append("&name=%s");
        String urlStr;
        try {
            urlStr = String.format(url.toString(), map.get("token"), map.get("openid"), URLEncoder.encode(map.get("name"), "utf-8"));
        }
        catch (UnsupportedEncodingException e) {
            log.error(e.getMessage(), e);
            return null;
        }
        log.info("跳转到页面 = {}", urlStr);
        return "redirect:" + urlStr;
    }
}
