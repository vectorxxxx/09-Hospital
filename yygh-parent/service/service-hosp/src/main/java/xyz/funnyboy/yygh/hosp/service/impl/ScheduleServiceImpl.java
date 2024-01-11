package xyz.funnyboy.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import xyz.funnyboy.yygh.hosp.repository.ScheduleRepository;
import xyz.funnyboy.yygh.hosp.service.DepartmentService;
import xyz.funnyboy.yygh.hosp.service.HospitalService;
import xyz.funnyboy.yygh.hosp.service.ScheduleService;
import xyz.funnyboy.yygh.model.hosp.Hospital;
import xyz.funnyboy.yygh.model.hosp.Schedule;
import xyz.funnyboy.yygh.vo.hosp.BookingScheduleRuleVo;
import xyz.funnyboy.yygh.vo.hosp.ScheduleQueryVo;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ScheduleServiceImpl
 *
 * @author VectorX
 * @version V1.0
 * @date 2024-01-08 11:05:05
 */
@Service
public class ScheduleServiceImpl implements ScheduleService
{
    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private HospitalService hospitalService;

    @Autowired
    private DepartmentService departmentService;

    @Override
    public void save(Map<String, Object> paramMap) {
        final Schedule schedule = JSONObject.parseObject(JSONObject.toJSONString(paramMap), Schedule.class);

        Schedule scheduleByHoscodeAndHosScheduleId = scheduleRepository.getScheduleByHoscodeAndHosScheduleId(schedule.getHoscode(), schedule.getHosScheduleId());
        if (scheduleByHoscodeAndHosScheduleId != null) {
            scheduleByHoscodeAndHosScheduleId.setUpdateTime(new Date());
            scheduleByHoscodeAndHosScheduleId.setStatus(1);
            scheduleByHoscodeAndHosScheduleId.setIsDeleted(0);
            scheduleRepository.save(schedule);
        }
        else {
            schedule.setCreateTime(new Date());
            schedule.setUpdateTime(new Date());
            schedule.setStatus(1);
            schedule.setIsDeleted(0);
            scheduleRepository.save(schedule);
        }
    }

    /**
     * 分页查询排班
     *
     * @param page            当前页
     * @param limit           每页大小
     * @param scheduleQueryVo 排班查询 VO
     * @return {@link Page}<{@link Schedule}>
     */
    @Override
    public Page<Schedule> findPageSchedule(int page, int limit, ScheduleQueryVo scheduleQueryVo) {
        // 分页参数
        final Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        final PageRequest pageRequest = PageRequest.of(page - 1, limit, sort);

        // 查询条件
        Schedule schedule = new Schedule();
        BeanUtils.copyProperties(scheduleQueryVo, schedule);
        schedule.setIsDeleted(0);
        schedule.setStatus(1);

        // 匹配器
        final ExampleMatcher exampleMatcher = ExampleMatcher
                .matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true);
        final Example<Schedule> example = Example.of(schedule, exampleMatcher);

        return scheduleRepository.findAll(example, pageRequest);
    }

    /**
     * 删除排班
     *
     * @param hoscode       医院编号
     * @param hosScheduleId 排班编号
     */
    @Override
    public void remove(String hoscode, String hosScheduleId) {
        final Schedule schedule = scheduleRepository.getScheduleByHoscodeAndHosScheduleId(hoscode, hosScheduleId);
        if (schedule != null) {
            scheduleRepository.delete(schedule);
        }
    }

    /**
     * 获取规则计划
     *
     * @param page    页码
     * @param limit   每页记录
     * @param hoscode 医院编号
     * @param depcode 科室编号
     * @return {@link Map}<{@link String}, {@link Object}>
     */
    @Override
    public Map<String, Object> getRuleSchedule(long page, long limit, String hoscode, String depcode) {
        // 查询条件
        final Criteria criteria = Criteria
                .where("hoscode")
                .is(hoscode)
                .and("depcode")
                .is(depcode);

        // 匹配条件
        final MatchOperation match = Aggregation.match(criteria);
        // 分组字段
        final GroupOperation groupOperation = Aggregation
                .group("workDate")
                .first("workDate")
                .as("workDate")
                // 统计号源数量
                .count()
                .as("docCount")
                .sum("reservedNumber")
                .as("reservedNumber")
                .sum("availableNumber")
                .as("availableNumber");
        // 排序
        final SortOperation sortOperation = Aggregation.sort(Sort.Direction.DESC, "workDate");
        // 分页
        final SkipOperation skipOperation = Aggregation.skip((page - 1) * limit);
        final LimitOperation limitOperation = Aggregation.limit(limit);

        // 查询分页数据
        final Aggregation agg = Aggregation.newAggregation(
                // 匹配条件
                match,
                // 分组字段
                groupOperation,
                // 排序
                sortOperation,
                // 分页
                skipOperation, limitOperation);
        final AggregationResults<BookingScheduleRuleVo> aggregationResults = mongoTemplate.aggregate(agg, Schedule.class, BookingScheduleRuleVo.class);
        final List<BookingScheduleRuleVo> bookingScheduleRuleVoList = aggregationResults.getMappedResults();

        // 查询总记录数
        final Aggregation totalAgg = Aggregation.newAggregation(
                // 匹配条件
                Aggregation.match(criteria),
                // 分组字段
                Aggregation.group("workDate"));
        final AggregationResults<BookingScheduleRuleVo> totalAggResults = mongoTemplate.aggregate(totalAgg, Schedule.class, BookingScheduleRuleVo.class);
        final int total = totalAggResults
                .getMappedResults()
                .size();

        // 设置日期对应的星期
        bookingScheduleRuleVoList.forEach(bookingScheduleRuleVo -> {
            final DateTime workDate = new DateTime(bookingScheduleRuleVo.getWorkDate());
            final String dayOfWeek = this.getDayOfWeek(workDate);
            bookingScheduleRuleVo.setDayOfWeek(dayOfWeek);
        });

        // 返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("bookingScheduleRuleVoList", bookingScheduleRuleVoList);
        result.put("total", total);

        // 查询医院名称
        final Hospital hospital = hospitalService.getByHoscode(hoscode);
        Map<String, String> baseMap = new HashMap<>();
        baseMap.put("hosname", hospital.getHosname());
        result.put("baseMap", baseMap);
        return result;
    }

    /**
     * 获取排班详情列表
     *
     * @param hoscode  医院编号
     * @param depcode  科室编号
     * @param workDate 工作日期
     * @return {@link List}<{@link Schedule}>
     */
    @Override
    public List<Schedule> getDetailSchedule(String hoscode, String depcode, String workDate) {
        // 查询排班数据
        final Date date = StringUtils.isEmpty(workDate) || "null".equals(workDate) ?
                          null :
                          new DateTime(workDate).toDate();
        final List<Schedule> scheduleList = scheduleRepository.findScheduleByHoscodeAndDepcodeAndWorkDate(hoscode, depcode, date);
        // 遍历集合，设置其他值：医院名称、科室名称、日期对应星期
        scheduleList.forEach(this::packageSchedule);
        return scheduleList;
    }

    /**
     * 封装排班详情其他值：医院名称、科室名称、日期对应星期
     *
     * @param schedule 附表
     */
    private void packageSchedule(Schedule schedule) {
        final String hoscode = schedule.getHoscode();
        final String depcode = schedule.getDepcode();
        final Date workDate = schedule.getWorkDate();
        // 设置医院名称
        schedule
                .getParam()
                .put("hosname", hospitalService.getByHoscode(hoscode));
        // 设置科室名称
        schedule
                .getParam()
                .put("depname", departmentService.getDepName(hoscode, depcode));
        // 设置日期对应星期
        schedule
                .getParam()
                .put("dayOfWeek", this.getDayOfWeek(new DateTime(workDate)));
    }

    /**
     * 根据日期获取周几数据
     *
     * @param dateTime
     * @return
     */
    private String getDayOfWeek(DateTime dateTime) {
        String dayOfWeek = "";
        switch (dateTime.getDayOfWeek()) {
            case DateTimeConstants.SUNDAY:
                dayOfWeek = "周日";
                break;
            case DateTimeConstants.MONDAY:
                dayOfWeek = "周一";
                break;
            case DateTimeConstants.TUESDAY:
                dayOfWeek = "周二";
                break;
            case DateTimeConstants.WEDNESDAY:
                dayOfWeek = "周三";
                break;
            case DateTimeConstants.THURSDAY:
                dayOfWeek = "周四";
                break;
            case DateTimeConstants.FRIDAY:
                dayOfWeek = "周五";
                break;
            case DateTimeConstants.SATURDAY:
                dayOfWeek = "周六";
            default:
                break;
        }
        return dayOfWeek;
    }
}
