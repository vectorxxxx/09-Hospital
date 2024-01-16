package xyz.funnyboy.yygh.order.service.impl;

import com.github.wxpay.sdk.WXPayUtil;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import xyz.funnyboy.yygh.enums.PaymentTypeEnum;
import xyz.funnyboy.yygh.enums.RefundStatusEnum;
import xyz.funnyboy.yygh.model.order.OrderInfo;
import xyz.funnyboy.yygh.model.order.PaymentInfo;
import xyz.funnyboy.yygh.model.order.RefundInfo;
import xyz.funnyboy.yygh.order.service.OrderService;
import xyz.funnyboy.yygh.order.service.PaymentService;
import xyz.funnyboy.yygh.order.service.RefundInfoService;
import xyz.funnyboy.yygh.order.service.WeixinService;
import xyz.funnyboy.yygh.order.utils.ConstantPropertiesUtils;
import xyz.funnyboy.yygh.order.utils.HttpClient;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author VectorX
 * @version V1.0
 * @date 2024-01-16 20:24:23
 */
@Service
@Slf4j
public class WeixinServiceImpl implements WeixinService
{
    @Autowired
    private OrderService orderService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RefundInfoService refundInfoService;

    /**
     * 生成微信支付二维码
     *
     * @param orderId 订单编号
     * @return {@link Map}<{@link String},{@link Object}>
     */
    @Override
    public Map<String, Object> createNative(Long orderId) {
        try {
            // 0 从redis获取数据
            final Map<String, Object> payMap = (Map<String, Object>) redisTemplate
                    .opsForValue()
                    .get(orderId.toString());
            if (payMap != null && !payMap.isEmpty()) {
                return payMap;
            }

            // 1 根据orderId获取订单信息
            final OrderInfo order = orderService.getById(orderId);

            // 2 向支付记录表添加信息
            paymentService.savePaymentInfo(order, PaymentTypeEnum.WEIXIN.getStatus());

            // 3 设置参数，
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("appid", ConstantPropertiesUtils.APPID);
            paramMap.put("mch_id", ConstantPropertiesUtils.PARTNER);
            // （1）获取随机字符串
            paramMap.put("nonce_str", WXPayUtil.generateNonceStr());
            paramMap.put("body", new DateTime(order.getReserveDate()).toString("yyyy-MM-dd") + (order.getReserveTime() == 0 ?
                                                                                                "上午" :
                                                                                                "下午") + "就诊于" + order.getDepname());
            paramMap.put("out_trade_no", order.getOutTradeNo());
            //paramMap.put("total_fee", order.getAmount().multiply(new BigDecimal("100")).longValue()+"");
            paramMap.put("total_fee", "1"); //为了测试，统一写成这个值
            paramMap.put("spbill_create_ip", ConstantPropertiesUtils.SPBILL_CREATE_IP);
            paramMap.put("notify_url", ConstantPropertiesUtils.NOTIFY_URL);
            paramMap.put("trade_type", "NATIVE");

            // 4 调用微信生成二维码接口, httpclient调用
            final HttpClient client = new HttpClient(ConstantPropertiesUtils.UNIFIED_ORDER_URL);
            // AP转换为XML字符串（自动添加签名）
            client.setXmlParam(WXPayUtil.generateSignedXml(paramMap, ConstantPropertiesUtils.PARTNERKEY));
            client.setHttps(true);
            client.post();

            // 5 返回相关数据
            final String xml = client.getContent();
            // （3）XML字符串转换为MAP
            final Map<String, String> resultMap = WXPayUtil.xmlToMap(xml);
            log.info("微信生成二维码接口返回结果：{}", resultMap);

            // 6 封装返回结果集
            Map<String, Object> map = new HashMap<>();
            map.put("orderId", orderId);
            map.put("totalFee", order.getAmount());
            map.put("resultCode", resultMap.get("result_code"));
            // 二维码地址
            map.put("codeUrl", resultMap.get("code_url"));

            // 7 缓存至Redis
            if (resultMap.get("result_code") != null) {
                redisTemplate
                        .opsForValue()
                        .set(orderId.toString(), map, 120, TimeUnit.MINUTES);
            }
            return map;
        }
        catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 查询支付状态
     *
     * @param orderId 订单编号
     * @return {@link Map}
     */
    @Override
    public Map<String, String> queryPayStatus(Long orderId) {
        try {
            // 1 根据orderId获取订单信息
            final OrderInfo order = orderService.getById(orderId);

            // 2 封装提交参数
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("appid", ConstantPropertiesUtils.APPID);
            paramMap.put("mch_id", ConstantPropertiesUtils.PARTNER);
            paramMap.put("nonce_str", WXPayUtil.generateNonceStr());
            paramMap.put("out_trade_no", order.getOutTradeNo());

            // 3 设置请求内容
            final HttpClient client = new HttpClient(ConstantPropertiesUtils.ORDER_QUERY_URL);
            client.setXmlParam(WXPayUtil.generateSignedXml(paramMap, ConstantPropertiesUtils.PARTNERKEY));
            client.setHttps(true);
            client.post();

            // 4 得到微信接口返回数据
            final String xml = client.getContent();
            final Map<String, String> resultMap = WXPayUtil.xmlToMap(xml);
            log.info("微信支付返回结果：{}", resultMap);
            return resultMap;
        }
        catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 退款
     *
     * @param orderId 订单编号
     * @return {@link Boolean}
     */
    @Override
    public Boolean refund(Long orderId) {
        try {
            // 获取支付记录信息
            final PaymentInfo paymentInfo = paymentService.getPaymentInfo(orderId, PaymentTypeEnum.WEIXIN.getStatus());

            // 添加信息到退款记录表
            RefundInfo refundInfo = refundInfoService.saveRefundInfo(paymentInfo);

            // 判断当前订单数据是否已经退款
            if (refundInfo
                    .getRefundStatus()
                    .intValue() == RefundStatusEnum.REFUND.getStatus()) {
                return true;
            }

            // 封装需要参数
            Map<String, String> paramMap = new HashMap<>();
            // 公众账号ID
            paramMap.put("appid", ConstantPropertiesUtils.APPID);
            // 商户编号
            paramMap.put("mch_id", ConstantPropertiesUtils.PARTNER);
            paramMap.put("nonce_str", WXPayUtil.generateNonceStr());
            // 微信订单号
            paramMap.put("transaction_id", paymentInfo.getTradeNo());
            // 商户订单编号
            paramMap.put("out_trade_no", paymentInfo.getOutTradeNo());
            // 商户退款单号
            paramMap.put("out_refund_no", "tk" + paymentInfo.getOutTradeNo());
            //       paramMap.put("total_fee",paymentInfoQuery.getTotalAmount().multiply(new BigDecimal("100")).longValue()+"");
            //       paramMap.put("refund_fee",paymentInfoQuery.getTotalAmount().multiply(new BigDecimal("100")).longValue()+"");
            paramMap.put("total_fee", "1");
            paramMap.put("refund_fee", "1");

            // 调用微信接口实现退款
            final HttpClient client = new HttpClient(ConstantPropertiesUtils.REFUND_URL);
            // 设置调用接口内容
            client.setXmlParam(WXPayUtil.generateSignedXml(paramMap, ConstantPropertiesUtils.PARTNERKEY));
            client.setHttps(true);
            // 设置证书信息
            client.setCert(true);
            // 注意！！！API证书调用或安装需要使用到密码，该密码的值为微信商户号（mch_id）
            client.setCertPassword(ConstantPropertiesUtils.PARTNER);
            client.post();

            // 接收返回数据
            final String xml = client.getContent();
            log.info("微信退款返回xml结果：{}", xml);

            // 处理返回数据
            final Map<String, String> resultMap = WXPayUtil.xmlToMap(xml);
            log.info("微信退款返回map结果：{}", resultMap);
            if (!resultMap.isEmpty() && "SUCCESS".equalsIgnoreCase(resultMap.get("result_code"))) {
                refundInfo.setRefundStatus(RefundStatusEnum.REFUND.getStatus());
                refundInfo.setTradeNo(resultMap.get("refund_id"));
                refundInfo.setCallbackTime(new Date());
                refundInfo.setCallbackContent(resultMap.toString());
                refundInfoService.updateById(refundInfo);
                return true;
            }
            return false;
        }
        catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }
}
