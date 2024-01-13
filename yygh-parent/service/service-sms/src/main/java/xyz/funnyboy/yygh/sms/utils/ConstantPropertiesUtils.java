package xyz.funnyboy.yygh.sms.utils;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @author VectorX
 * @version V1.0
 * @date 2024-01-13 14:46:33
 */
@Component
// 解决配置文件中中文乱码的问题，注意不能放在默认的application.properties中，必须新建配置文件才行
@PropertySource(value = "classpath:application-sms.properties",
                encoding = "UTF-8")
public class ConstantPropertiesUtils implements InitializingBean
{
    @Value("${aliyun.sms.endpoint}")
    private String endpoint;

    @Value("${aliyun.sms.accessKeyId}")
    private String accessKeyId;

    @Value("${aliyun.sms.secret}")
    private String secret;

    @Value("${aliyun.sms.signname}")
    private String signName;

    @Value("${aliyun.sms.templatecode}")
    private String templateCode;

    public static String END_POINT;
    public static String ACCESS_KEY_ID;
    public static String SECRECT;
    public static String SIGN_NAME;
    public static String TEMPLATE_CODE;

    @Override
    public void afterPropertiesSet() throws Exception {
        END_POINT = endpoint;
        ACCESS_KEY_ID = accessKeyId;
        SECRECT = secret;
        SIGN_NAME = signName;
        TEMPLATE_CODE = templateCode;
    }
}
