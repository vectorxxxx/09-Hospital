package xyz.funnyboy.yygh.sms.service;

import xyz.funnyboy.yygh.vo.sms.SmsVo;

/**
 * @author VectorX
 * @version V1.0
 * @date 2024-01-13 14:48:14
 */
public interface SmsService
{
    /**
     * 发送验证码
     *
     * @param phone 电话号码
     * @param code  验证码
     * @return boolean
     */
    boolean send(String phone, String code);

    /**
     * 使用MQ发送短信
     *
     * @param smsVo 短信VO
     * @return boolean
     */
    boolean send(SmsVo smsVo);
}
