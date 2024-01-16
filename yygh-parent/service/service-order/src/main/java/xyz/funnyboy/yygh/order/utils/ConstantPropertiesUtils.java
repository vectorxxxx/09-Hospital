package xyz.funnyboy.yygh.order.utils;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author VectorX
 * @version V1.0
 * @date 2024-01-16 20:10:54
 */
@Component
public class ConstantPropertiesUtils implements InitializingBean
{

    @Value("${weixin.appid}")
    private String appid;

    @Value("${weixin.partner}")
    private String partner;

    @Value("${weixin.partnerkey}")
    private String partnerkey;

    @Value("${weixin.spbillcreateip}")
    private String spbillCreateIp;

    @Value("${weixin.notifyurl}")
    private String notifyUrl;

    @Value("${weixin.unifiedorderurl}")
    private String unifiedOrderURL;

    @Value("${weixin.orderqueryurl}")
    private String orderQueryURL;

    @Value("${weixin.refundurl}")
    private String refundURL;

    @Value("${weixin.cert}")
    private String cert;

    public static String APPID;
    public static String PARTNER;
    public static String PARTNERKEY;
    public static String SPBILL_CREATE_IP;
    public static String NOTIFY_URL;
    public static String UNIFIED_ORDER_URL;
    public static String ORDER_QUERY_URL;
    public static String REFUND_URL;
    public static String CERT;

    @Override
    public void afterPropertiesSet() throws Exception {
        APPID = appid;
        PARTNER = partner;
        PARTNERKEY = partnerkey;
        SPBILL_CREATE_IP = spbillCreateIp;
        NOTIFY_URL = notifyUrl;
        UNIFIED_ORDER_URL = unifiedOrderURL;
        ORDER_QUERY_URL = orderQueryURL;
        REFUND_URL = refundURL;
        CERT = cert;
    }

}
