package xyz.funnyboy.yygh.user.utils;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author VectorX
 * @version V1.0
 * @date 2024-01-13 18:23:05
 */
@Component
public class ConstantWxPropertiesUtils implements InitializingBean
{

    @Value("${wx.open.app_id}")
    private String appId;

    @Value("${wx.open.app_secret}")
    private String appSecret;

    @Value("${wx.open.redirect_uri}")
    private String redirect_uri;

    @Value("${yygh.baseUrl}")
    private String yyghBaseUrl;

    public static String WX_OPEN_APP_ID;
    public static String WX_OPEN_APP_SECRET;
    public static String WX_OPEN_REDIRECT_URI;
    public static String YYGH_BASE_URL;

    @Override
    public void afterPropertiesSet() throws Exception {
        WX_OPEN_APP_ID = appId;
        WX_OPEN_APP_SECRET = appSecret;
        WX_OPEN_REDIRECT_URI = redirect_uri;
        YYGH_BASE_URL = yyghBaseUrl;
    }
}

