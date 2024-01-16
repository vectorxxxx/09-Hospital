package xyz.funnyboy.yygh.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.funnyboy.common.rabbit.constant.MQConst;
import xyz.funnyboy.common.rabbit.service.RabbitService;
import xyz.funnyboy.yygh.common.exception.YyghException;
import xyz.funnyboy.yygh.common.helper.HttpRequestHelper;
import xyz.funnyboy.yygh.common.result.ResultCodeEnum;
import xyz.funnyboy.yygh.enums.OrderStatusEnum;
import xyz.funnyboy.yygh.hosp.client.HospitalFeignClient;
import xyz.funnyboy.yygh.model.order.OrderInfo;
import xyz.funnyboy.yygh.model.user.Patient;
import xyz.funnyboy.yygh.order.mapper.OrderInfoMapper;
import xyz.funnyboy.yygh.order.service.OrderService;
import xyz.funnyboy.yygh.user.client.PatientFeignClient;
import xyz.funnyboy.yygh.vo.hosp.ScheduleOrderVo;
import xyz.funnyboy.yygh.vo.order.OrderMqVo;
import xyz.funnyboy.yygh.vo.order.SignInfoVo;
import xyz.funnyboy.yygh.vo.sms.SmsVo;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @author VectorX
 * @version V1.0
 * @date 2024-01-15 23:28:20
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo> implements OrderService
{
    @Autowired
    private PatientFeignClient patientFeignClient;

    @Autowired
    private HospitalFeignClient hospitalFeignClient;

    @Autowired
    private RabbitService rabbitService;

    /**
     * 保存订单
     *
     * @param scheduleId 排班 ID
     * @param patientId  就诊人 ID
     * @return {@link Long}
     */
    @Override
    public Long saveOrder(String scheduleId, Long patientId) {
        // 获取排班信息
        final ScheduleOrderVo scheduleOrderVo = hospitalFeignClient.getScheduleOrderVo(scheduleId);

        // 判断当前时间是否还可以预约
        if (new DateTime(scheduleOrderVo.getStartTime()).isAfterNow() || new DateTime(scheduleOrderVo.getEndTime()).isBeforeNow()) {
            throw new YyghException(ResultCodeEnum.TIME_NO);
        }

        // 获取签名信息
        final SignInfoVo signInfo = hospitalFeignClient.getSignInfo(scheduleOrderVo.getHoscode());

        // 获取就诊人信息
        final Patient patient = patientFeignClient.getPatient(patientId);

        // 添加到订单表
        final OrderInfo orderInfo = new OrderInfo();
        BeanUtils.copyProperties(scheduleOrderVo, orderInfo);
        orderInfo.setOutTradeNo(System.currentTimeMillis() + "" + new Random().nextInt(100));
        orderInfo.setScheduleId(scheduleId);
        orderInfo.setUserId(patient.getUserId());
        orderInfo.setPatientId(patientId);
        orderInfo.setPatientName(patient.getName());
        orderInfo.setPatientPhone(patient.getPhone());
        orderInfo.setOrderStatus(OrderStatusEnum.UNPAID.getStatus());
        baseMapper.insert(orderInfo);

        // 设置调用医院接口需要参数，参数放到map集合
        Map<String, Object> paramMap = new HashMap<>();
        // 订单信息
        paramMap.put("hoscode", orderInfo.getHoscode());
        paramMap.put("depcode", orderInfo.getDepcode());
        paramMap.put("hosScheduleId", scheduleOrderVo.getHosScheduleId());
        paramMap.put("reserveDate", new DateTime(orderInfo.getReserveDate()).toString("yyyy-MM-dd"));
        paramMap.put("reserveTime", orderInfo.getReserveTime());
        paramMap.put("amount", orderInfo.getAmount());
        // 就诊人信息
        paramMap.put("name", patient.getName());
        paramMap.put("sex", patient.getSex());
        paramMap.put("birthdate", patient.getBirthdate());
        paramMap.put("phone", patient.getPhone());
        paramMap.put("isMarry", patient.getIsMarry());
        paramMap.put("provinceCode", patient.getProvinceCode());
        paramMap.put("cityCode", patient.getCityCode());
        paramMap.put("districtCode", patient.getDistrictCode());
        paramMap.put("address", patient.getAddress());
        paramMap.put("certificatesType", patient.getCertificatesType());
        paramMap.put("certificatesNo", patient.getCertificatesNo());
        // 联系人信息
        paramMap.put("contactsName", patient.getContactsName());
        paramMap.put("contactsCertificatesType", patient.getContactsCertificatesType());
        paramMap.put("contactsCertificatesNo", patient.getContactsCertificatesNo());
        paramMap.put("contactsPhone", patient.getContactsPhone());

        // 其他信息
        paramMap.put("timestamp", HttpRequestHelper.getTimestamp());
        paramMap.put("sign", HttpRequestHelper.getSign(paramMap, signInfo.getSignKey()));

        // 调用医院接口，实现预约挂号操作
        final JSONObject result = HttpRequestHelper.sendRequest(paramMap, signInfo.getApiUrl() + "/order/submitOrder");

        // 处理返回结果
        if (result.getInteger("code") == 200) {
            JSONObject jsonObject = result.getJSONObject("data");
            // 预约记录唯一标识（医院预约记录主键）
            final String hosRecordId = jsonObject.getString("hosRecordId");
            // 预约序号
            final Integer number = jsonObject.getInteger("number");
            // 排班可预约数
            final Integer reservedNumber = jsonObject.getInteger("reservedNumber");
            // 排班剩余预约数
            final Integer availableNumber = jsonObject.getInteger("availableNumber");
            // 取号时间
            final String fetchTime = jsonObject.getString("fetchTime");
            // 取号地址
            final String fetchAddress = jsonObject.getString("fetchAddress");

            // 更新订单
            orderInfo.setHosRecordId(hosRecordId);
            orderInfo.setNumber(number);
            orderInfo.setFetchTime(fetchTime);
            orderInfo.setFetchAddress(fetchAddress);
            baseMapper.updateById(orderInfo);

            // 发送mq信息更新号源和短信通知
            // 发送mq信息更新号源
            final OrderMqVo orderMqVo = new OrderMqVo();
            orderMqVo.setScheduleId(scheduleId);
            orderMqVo.setReservedNumber(reservedNumber);
            orderMqVo.setAvailableNumber(availableNumber);
            // 短信提示
            final SmsVo smsVo = new SmsVo();
            smsVo.setPhone(orderInfo.getPatientPhone());
            final HashMap<String, Object> param = new HashMap<String, Object>()
            {
                private static final long serialVersionUID = 2537007065794860846L;

                {
                    put("title", orderInfo.getHosname() + "|" + orderInfo.getDepname() + "|" + orderInfo.getTitle());
                    put("amount", orderInfo.getAmount());
                    put("reserveDate", new DateTime(orderInfo.getReserveDate()).toString("yyyy-MM-dd") + (orderInfo.getReserveTime() == 0 ?
                                                                                                          "上午" :
                                                                                                          "下午"));
                    put("name", orderInfo.getPatientName());
                    put("quitTime", new DateTime(orderInfo.getQuitTime()).toString("yyyy-MM-dd HH:mm"));
                }
            };
            smsVo.setParam(param);
            orderMqVo.setSmsVo(smsVo);

            rabbitService.sendMessage(MQConst.EXCHANGE_DIRECT_ORDER, MQConst.ROUTING_ORDER, orderMqVo);
        }
        else {
            throw new YyghException(result.getString("message"), ResultCodeEnum.FAIL.getCode());
        }
        return orderInfo.getId();
    }
}
