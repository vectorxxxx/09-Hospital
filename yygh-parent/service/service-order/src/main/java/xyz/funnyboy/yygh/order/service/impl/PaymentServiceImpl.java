package xyz.funnyboy.yygh.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.funnyboy.yygh.common.exception.YyghException;
import xyz.funnyboy.yygh.common.helper.HttpRequestHelper;
import xyz.funnyboy.yygh.common.result.ResultCodeEnum;
import xyz.funnyboy.yygh.enums.OrderStatusEnum;
import xyz.funnyboy.yygh.enums.PaymentStatusEnum;
import xyz.funnyboy.yygh.enums.PaymentTypeEnum;
import xyz.funnyboy.yygh.hosp.client.HospitalFeignClient;
import xyz.funnyboy.yygh.model.order.OrderInfo;
import xyz.funnyboy.yygh.model.order.PaymentInfo;
import xyz.funnyboy.yygh.order.mapper.PaymentInfoMapper;
import xyz.funnyboy.yygh.order.service.OrderService;
import xyz.funnyboy.yygh.order.service.PaymentService;
import xyz.funnyboy.yygh.vo.order.SignInfoVo;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author VectorX
 * @version V1.0
 * @date 2024-01-16 20:17:25
 */
@Service
@Slf4j
public class PaymentServiceImpl extends ServiceImpl<PaymentInfoMapper, PaymentInfo> implements PaymentService
{
    @Autowired
    private OrderService orderService;

    @Autowired
    private HospitalFeignClient hospitalFeignClient;

    /**
     * 保存交易记录
     *
     * @param order       订单信息
     * @param paymentType 支付类型（1：微信 2：支付宝）
     */
    @Override
    public void savePaymentInfo(OrderInfo order, Integer paymentType) {
        // 根据订单id和支付类型，查询支付记录表是否存在相同订单
        final Integer count = baseMapper.selectCount(new LambdaQueryWrapper<PaymentInfo>()
                .eq(PaymentInfo::getOrderId, order.getId())
                .eq(PaymentInfo::getPaymentType, paymentType));
        if (count > 0) {
            return;
        }

        // 添加记录
        final PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setCreateTime(new Date());
        paymentInfo.setOrderId(order.getId());
        paymentInfo.setPaymentType(paymentType);
        paymentInfo.setOutTradeNo(order.getOutTradeNo());
        paymentInfo.setPaymentStatus(PaymentStatusEnum.UNPAID.getStatus());
        paymentInfo.setSubject(new DateTime(order.getReserveDate()).toString("yyyy-MM-dd") + "|" + order.getHosname() + "|" + order.getDepname() + "|" + order.getTitle());
        paymentInfo.setTotalAmount(order.getAmount());
        baseMapper.insert(paymentInfo);

    }

    /**
     * 更新订单状态
     *
     * @param outTradeNo 交易编号
     * @param resultMap  结果
     */
    @Override
    public void paySuccess(String outTradeNo, Map<String, String> resultMap) {
        //1 根据订单编号得到支付记录
        final PaymentInfo paymentInfo = baseMapper.selectOne(new LambdaQueryWrapper<PaymentInfo>()
                .eq(PaymentInfo::getOutTradeNo, outTradeNo)
                .eq(PaymentInfo::getPaymentType, PaymentTypeEnum.WEIXIN.getStatus()));
        if (paymentInfo == null) {
            throw new YyghException(ResultCodeEnum.PARAM_ERROR);
        }
        if (paymentInfo
                .getPaymentStatus()
                .intValue() != PaymentStatusEnum.UNPAID.getStatus()) {
            return;
        }

        //2 更新支付记录信息
        paymentInfo.setPaymentStatus(PaymentStatusEnum.PAID.getStatus());
        paymentInfo.setTradeNo(resultMap.get("transaction_id"));
        paymentInfo.setCallbackTime(new Date());
        paymentInfo.setCallbackContent(resultMap.toString());
        baseMapper.updateById(paymentInfo);

        //3 更新订单信息
        final OrderInfo orderInfo = orderService.getById(paymentInfo.getOrderId());
        orderInfo.setOrderStatus(OrderStatusEnum.PAID.getStatus());
        orderService.updateById(orderInfo);

        //4 调用医院接口，更新订单支付信息
        final SignInfoVo signInfo = hospitalFeignClient.getSignInfo(orderInfo.getHoscode());
        Map<String, Object> reqMap = new HashMap<>();
        reqMap.put("hoscode", orderInfo.getHoscode());
        reqMap.put("hosRecordId", orderInfo.getHosRecordId());
        reqMap.put("timestamp", HttpRequestHelper.getTimestamp());
        final String sign = HttpRequestHelper.getSign(reqMap, signInfo.getSignKey());
        reqMap.put("sign", sign);
        final JSONObject result = HttpRequestHelper.sendRequest(reqMap, signInfo.getApiUrl() + "/order/updatePayStatus");
        log.info("调用医院接口，更新订单支付信息，结果：{}", result);
    }

    /**
     * 获取支付记录
     *
     * @param orderId     订单编号
     * @param paymentType 付款方式
     * @return {@link PaymentInfo}
     */
    @Override
    public PaymentInfo getPaymentInfo(Long orderId, Integer paymentType) {
        return baseMapper.selectOne(new LambdaQueryWrapper<PaymentInfo>()
                .eq(PaymentInfo::getOrderId, orderId)
                .eq(PaymentInfo::getPaymentType, paymentType));
    }
}
