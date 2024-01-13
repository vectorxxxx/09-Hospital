package xyz.funnyboy.yygh.sms.service.impl;

import com.alibaba.fastjson.JSON;
import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.dysmsapi20170525.models.SendSmsResponseBody;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teautil.models.RuntimeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import xyz.funnyboy.yygh.sms.service.SmsService;
import xyz.funnyboy.yygh.sms.utils.ConstantPropertiesUtils;

import java.util.HashMap;

/**
 * @author VectorX
 * @version V1.0
 * @date 2024-01-13 14:48:58
 */
@Service
public class SmsServiceImpl implements SmsService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(SmsServiceImpl.class);

    /**
     * 使用AK&SK初始化账号Client
     *
     * @param accessKeyId
     * @param accessKeySecret
     * @return Client
     * @throws Exception
     */
    private Client createClient(String accessKeyId, String accessKeySecret) throws Exception {
        Config config = new Config()
                // 必填，您的 AccessKey ID
                .setAccessKeyId(accessKeyId)
                // 必填，您的 AccessKey Secret
                .setAccessKeySecret(accessKeySecret);
        // Endpoint 请参考 https://api.aliyun.com/product/Dysmsapi
        config.endpoint = ConstantPropertiesUtils.END_POINT;
        return new Client(config);
    }

    /**
     * 发送验证码
     *
     * @param phone 电话号码
     * @param code  验证码
     * @return boolean
     */
    @Override
    public boolean send(String phone, String code) {
        try {
            final HashMap<String, String> param = new HashMap<>();
            param.put("code", code);

            SendSmsRequest sendSmsRequest = new SendSmsRequest()
                    // 短信签名名称
                    .setSignName(ConstantPropertiesUtils.SIGN_NAME)
                    // 短信模板Code
                    .setTemplateCode(ConstantPropertiesUtils.TEMPLATE_CODE)
                    // 接收短信的手机号码
                    .setPhoneNumbers(phone)
                    // 短信模板变量对应的实际值。支持传入多个参数
                    .setTemplateParam(JSON.toJSONString(param));

            Client client = createClient(ConstantPropertiesUtils.ACCESS_KEY_ID, ConstantPropertiesUtils.SECRECT);
            final SendSmsResponse resp = client.sendSmsWithOptions(sendSmsRequest, new RuntimeOptions());
            final SendSmsResponseBody responseBody = resp.getBody();

            if (resp.getStatusCode() != 200 || !"OK".equals(responseBody.getCode())) {
                LOGGER.error("发送短信失败，错误码：{}，错误信息：{}", responseBody.getCode(), responseBody.getMessage());
                return false;
            }

            return true;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return false;
        }
    }
}
