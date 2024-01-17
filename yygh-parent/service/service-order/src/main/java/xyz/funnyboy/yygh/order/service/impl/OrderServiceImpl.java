package xyz.funnyboy.yygh.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
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
import xyz.funnyboy.yygh.order.service.WeixinService;
import xyz.funnyboy.yygh.user.client.PatientFeignClient;
import xyz.funnyboy.yygh.vo.hosp.ScheduleOrderVo;
import xyz.funnyboy.yygh.vo.order.*;
import xyz.funnyboy.yygh.vo.sms.SmsVo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * @author VectorX
 * @version V1.0
 * @date 2024-01-15 23:28:20
 */
@Service
@Slf4j
public class OrderServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo> implements OrderService
{
    @Autowired
    private PatientFeignClient patientFeignClient;

    @Autowired
    private HospitalFeignClient hospitalFeignClient;

    @Autowired
    private RabbitService rabbitService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private WeixinService weixinService;

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

    /**
     * 选择页面
     *
     * @param pageParam    页面参数
     * @param orderQueryVo 订单查询 VO
     * @return {@link IPage}<{@link OrderInfo}>
     */
    @Override
    public IPage<OrderInfo> selectPage(Page<OrderInfo> pageParam, OrderQueryVo orderQueryVo) {
        // orderQueryVo获取条件值
        final String name = orderQueryVo.getKeyword();
        final String outTradeNo = orderQueryVo.getOutTradeNo();
        final Long patientId = orderQueryVo.getPatientId();
        final String orderStatus = orderQueryVo.getOrderStatus();
        final String reserveDate = orderQueryVo.getReserveDate();
        final String createTimeBegin = orderQueryVo.getCreateTimeBegin();
        final String createTimeEnd = orderQueryVo.getCreateTimeEnd();

        // 对条件值进行非空判断
        final LambdaQueryWrapper<OrderInfo> queryWrapper = new LambdaQueryWrapper<OrderInfo>()
                .like(!StringUtils.isEmpty(name), OrderInfo::getHosname, name)
                .eq(!StringUtils.isEmpty(outTradeNo), OrderInfo::getOutTradeNo, outTradeNo)
                .eq(!StringUtils.isEmpty(patientId), OrderInfo::getPatientId, patientId)
                .eq(!StringUtils.isEmpty(orderStatus), OrderInfo::getOrderStatus, orderStatus)
                .ge(!StringUtils.isEmpty(reserveDate), OrderInfo::getReserveDate, reserveDate)
                .ge(!StringUtils.isEmpty(createTimeBegin), OrderInfo::getCreateTime, createTimeBegin)
                .le(!StringUtils.isEmpty(createTimeEnd), OrderInfo::getCreateTime, createTimeEnd)
                .orderByDesc(OrderInfo::getReserveDate)
                .orderByDesc(OrderInfo::getReserveTime)
                .orderByDesc(OrderInfo::getUpdateTime)
                .orderByDesc(OrderInfo::getCreateTime);

        // 编号变成对应值封装
        final Page<OrderInfo> pages = baseMapper.selectPage(pageParam, queryWrapper);
        pages
                .getRecords()
                .forEach(this::packOrderInfo);
        return pages;
    }

    /**
     * 根据订单ID获取订单信息
     *
     * @param orderId 订单ID
     * @return {@link OrderInfo}
     */
    @Override
    public OrderInfo getOrderInfo(String orderId) {
        return this.packOrderInfo(baseMapper.selectById(orderId));
    }

    /**
     * 订单详情
     *
     * @param orderId 订单编号
     * @return {@link Map}<{@link String}, {@link Object}>
     */
    @Override
    public Map<String, Object> show(Long orderId) {
        Map<String, Object> map = new HashMap<>();
        final OrderInfo orderInfo = this.packOrderInfo(baseMapper.selectById(orderId));
        map.put("orderInfo", orderInfo);
        map.put("patient", patientFeignClient.getPatient(orderInfo.getPatientId()));
        return map;
    }

    /**
     * 取消订单
     *
     * @param orderId 订单编号
     * @return {@link Boolean}
     */
    @Override
    public Boolean cancelOrder(Long orderId) {
        // 获取订单信息
        final OrderInfo orderInfo = orderService.getById(orderId);

        // 判断是否取消
        if (new DateTime(orderInfo.getQuitTime()).isBeforeNow()) {
            throw new YyghException(ResultCodeEnum.CANCEL_ORDER_NO);
        }

        // 调用医院接口实现预约取消
        final SignInfoVo signInfo = hospitalFeignClient.getSignInfo(orderInfo.getHoscode());
        if (signInfo == null) {
            throw new YyghException(ResultCodeEnum.PARAM_ERROR);
        }
        Map<String, Object> reqMap = new HashMap<>();
        reqMap.put("hoscode", orderInfo.getHoscode());
        reqMap.put("hosRecordId", orderInfo.getHosRecordId());
        reqMap.put("timestamp", HttpRequestHelper.getTimestamp());
        reqMap.put("sign", HttpRequestHelper.getSign(reqMap, signInfo.getSignKey()));
        JSONObject result = HttpRequestHelper.sendRequest(reqMap, signInfo.getApiUrl() + "/order/updateCancelStatus");
        log.info("result: " + result.toJSONString());

        // 根据医院接口返回数据
        if (result.getInteger("code") != 200) {
            throw new YyghException(result.getString("message"), ResultCodeEnum.FAIL.getCode());
        }

        // 判断当前订单是否可以取消
        if (orderInfo
                .getOrderStatus()
                .intValue() == OrderStatusEnum.PAID
                .getStatus()
                .intValue()) {
            // 更新订单状态
            final Boolean refund = weixinService.refund(orderId);
            if (!refund) {
                throw new YyghException(ResultCodeEnum.CANCEL_ORDER_FAIL);
            }

            // 发送mq更新预约数量
            OrderMqVo orderMqVo = new OrderMqVo();
            orderMqVo.setScheduleId(orderInfo.getScheduleId());

            // 短信提示
            SmsVo smsVo = new SmsVo();
            smsVo.setPhone(orderInfo.getPatientPhone());
            final HashMap<String, Object> param = new HashMap<String, Object>()
            {
                private static final long serialVersionUID = 6027451149102592897L;

                {
                    put("title", orderInfo.getHosname() + "|" + orderInfo.getDepname() + orderInfo.getTitle());
                    put("reserveDate", new DateTime(orderInfo.getReserveDate()).toString("yyyy-MM-dd") + (orderInfo.getReserveTime() == 0 ?
                                                                                                          "上午" :
                                                                                                          "下午"));
                    put("name", orderInfo.getPatientName());
                }
            };
            smsVo.setParam(param);
            orderMqVo.setSmsVo(smsVo);
            rabbitService.sendMessage(MQConst.EXCHANGE_DIRECT_ORDER, MQConst.ROUTING_ORDER, orderMqVo);
        }
        return null;
    }

    /**
     * 就诊提醒
     */
    @Override
    public void patientTips() {
        // 查询当天未取消的订单
        final List<OrderInfo> orderInfoList = baseMapper.selectList(new LambdaQueryWrapper<OrderInfo>()
                .eq(OrderInfo::getReserveDate, new DateTime().toString("yyyy-MM-dd"))
                .ne(OrderInfo::getOrderStatus, OrderStatusEnum.CANCLE.getStatus()));

        // 遍历发短信
        orderInfoList.forEach(orderInfo -> {
            SmsVo smsVo = new SmsVo();
            smsVo.setPhone(orderInfo.getPatientPhone());
            HashMap<String, Object> param = new HashMap<String, Object>()
            {
                private static final long serialVersionUID = 6027451149102592897L;

                {
                    put("title", orderInfo.getHosname() + "|" + orderInfo.getDepname() + orderInfo.getTitle());
                    put("reserveDate", new DateTime(orderInfo.getReserveDate()).toString("yyyy-MM-dd") + (orderInfo.getReserveTime() == 0 ?
                                                                                                          "上午" :
                                                                                                          "下午"));
                    put("name", orderInfo.getPatientName());
                }
            };
            smsVo.setParam(param);
            rabbitService.sendMessage(MQConst.EXCHANGE_DIRECT_SMS, MQConst.ROUTING_SMS_ITEM, smsVo);
        });
    }

    /**
     * 订单统计
     *
     * @param orderCountQueryVo 订单统计查询 VO
     * @return {@link Map}<{@link String}, {@link Object}>
     */
    @Override
    public Map<String, Object> getCountMap(OrderCountQueryVo orderCountQueryVo) {
        // 调用mapper方法得到数据
        final List<OrderCountVo> orderCountVoList = baseMapper.selectOrderCount(orderCountQueryVo);

        // 获取x需要数据，日期数据  list集合
        final List<String> dateList = orderCountVoList
                .stream()
                .map(OrderCountVo::getReserveDate)
                .collect(Collectors.toList());

        // 获取y需要数据，具体数量  list集合
        final List<Integer> countList = orderCountVoList
                .stream()
                .map(OrderCountVo::getCount)
                .collect(Collectors.toList());

        // 封装数据
        Map<String, Object> countMap = new HashMap<>();
        countMap.put("dateList", dateList);
        countMap.put("countList", countList);
        return countMap;
    }

    /**
     * 包装订单信息
     *
     * @param orderInfo 订单信息
     * @return {@link OrderInfo}
     */
    private OrderInfo packOrderInfo(OrderInfo orderInfo) {
        orderInfo
                .getParam()
                .put("orderStatusString", OrderStatusEnum.getStatusNameByStatus(orderInfo.getOrderStatus()));
        return orderInfo;
    }
}
