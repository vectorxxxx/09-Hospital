package xyz.funnyboy.yygh.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.funnyboy.yygh.enums.RefundStatusEnum;
import xyz.funnyboy.yygh.model.order.PaymentInfo;
import xyz.funnyboy.yygh.model.order.RefundInfo;
import xyz.funnyboy.yygh.order.mapper.RefundInfoMapper;
import xyz.funnyboy.yygh.order.service.PaymentService;
import xyz.funnyboy.yygh.order.service.RefundInfoService;

import java.util.Date;

/**
 * @author VectorX
 * @version V1.0
 * @date 2024-01-16 21:38:42
 */
@Service
public class RefundInfoServiceImpl extends ServiceImpl<RefundInfoMapper, RefundInfo> implements RefundInfoService
{
    @Autowired
    private PaymentService paymentService;

    /**
     * 保存退款信息
     *
     * @param paymentInfo 付款信息
     * @return {@link RefundInfo}
     */
    @Override
    public RefundInfo saveRefundInfo(PaymentInfo paymentInfo) {
        RefundInfo refundInfo = baseMapper.selectOne(new LambdaQueryWrapper<RefundInfo>()
                .eq(RefundInfo::getOrderId, paymentInfo.getOrderId())
                .eq(RefundInfo::getPaymentType, paymentInfo.getPaymentType()));
        if (refundInfo != null) {
            return refundInfo;
        }

        // 添加记录
        refundInfo = new RefundInfo();
        refundInfo.setCreateTime(new Date());
        refundInfo.setOrderId(paymentInfo.getOrderId());
        refundInfo.setPaymentType(paymentInfo.getPaymentType());
        refundInfo.setOutTradeNo(paymentInfo.getOutTradeNo());
        refundInfo.setRefundStatus(RefundStatusEnum.UNREFUND.getStatus());
        refundInfo.setSubject(paymentInfo.getSubject());
        refundInfo.setTotalAmount(paymentInfo.getTotalAmount());
        baseMapper.insert(refundInfo);
        return refundInfo;
    }
}
