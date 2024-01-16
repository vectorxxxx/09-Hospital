package xyz.funnyboy.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import xyz.funnyboy.yygh.common.exception.YyghException;
import xyz.funnyboy.yygh.common.result.ResultCodeEnum;
import xyz.funnyboy.yygh.hosp.repository.ScheduleRepository;
import xyz.funnyboy.yygh.hosp.service.DepartmentService;
import xyz.funnyboy.yygh.hosp.service.HospitalService;
import xyz.funnyboy.yygh.hosp.service.ScheduleService;
import xyz.funnyboy.yygh.model.hosp.BookingRule;
import xyz.funnyboy.yygh.model.hosp.Department;
import xyz.funnyboy.yygh.model.hosp.Hospital;
import xyz.funnyboy.yygh.model.hosp.Schedule;
import xyz.funnyboy.yygh.vo.hosp.BookingScheduleRuleVo;
import xyz.funnyboy.yygh.vo.hosp.ScheduleOrderVo;
import xyz.funnyboy.yygh.vo.hosp.ScheduleQueryVo;

import java.util.*;
import java.util.stream.Collectors;

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
     * 获取可预约的排班数据
     *
     * @param page    页码
     * @param limit   页面大小
     * @param hoscode 医院编号
     * @param depcode 科室编号
     * @return {@link Map}<{@link String}, {@link Object}>
     */
    @Override
    public Map<String, Object> getBookingScheduleRule(int page, int limit, String hoscode, String depcode) {
        // 获取预约规则
        final Hospital hospital = hospitalService.getByHoscode(hoscode);
        if (hospital == null) {
            throw new YyghException(ResultCodeEnum.DATA_ERROR);
        }
        final BookingRule bookingRule = hospital.getBookingRule();

        // 获取可预约日期的数据（分页）
        final IPage<Date> iPage = this.getListDate(page, limit, bookingRule);
        final List<Date> dateList = iPage.getRecords();

        // 获取可预约日期里面科室的剩余预约数
        final Criteria criteria = Criteria
                .where("hoscode")
                .is(hoscode)
                .and("depcode")
                .is(depcode)
                .and("workDate")
                .in(dateList);
        final Aggregation aggregation = Aggregation.newAggregation(
                // 查询条件
                Aggregation.match(criteria),
                // 分页查询
                Aggregation
                        .group("workDate")
                        .first("workDate")
                        .as("workDate")
                        // 统计号源数量
                        .count()
                        .as("docCount")
                        // 剩余预约数
                        .sum("availableNumber")
                        .as("availableNumber")
                        // 总预约数
                        .sum("reservedNumber")
                        .as("reservedNumber"));
        final AggregationResults<BookingScheduleRuleVo> aggregateResult = mongoTemplate.aggregate(
                // 聚合
                aggregation,
                // 输入类型
                Schedule.class,
                // 输出类型
                BookingScheduleRuleVo.class);
        final List<BookingScheduleRuleVo> scheduleRuleVoList = aggregateResult.getMappedResults();

        // 合并数据  map集合 key日期  value预约规则和剩余数量等
        Map<Date, BookingScheduleRuleVo> bookingScheduleRuleVoMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(scheduleRuleVoList)) {
            bookingScheduleRuleVoMap = scheduleRuleVoList
                    .stream()
                    .collect(Collectors.toMap(BookingScheduleRuleVo::getWorkDate, bookingScheduleRuleVo -> bookingScheduleRuleVo));
        }

        // 获取可预约排班规则
        List<BookingScheduleRuleVo> bookingScheduleRuleVoList = new ArrayList<>();
        for (int i = 0, len = dateList.size(); i < len; i++) {
            final Date date = dateList.get(i);
            BookingScheduleRuleVo bookingScheduleRuleVo = bookingScheduleRuleVoMap.getOrDefault(date, null);
            // 如果当天没有排班医生
            if (bookingScheduleRuleVo == null) {
                bookingScheduleRuleVo = new BookingScheduleRuleVo();
                bookingScheduleRuleVo.setDocCount(0);
                bookingScheduleRuleVo.setAvailableNumber(-1);
            }
            bookingScheduleRuleVo.setWorkDate(date);
            bookingScheduleRuleVo.setWorkDateMd(date);
            // 计算当前预约日期对应星期
            bookingScheduleRuleVo.setDayOfWeek(this.getDayOfWeek(new DateTime(date)));

            // 最后一页最后一条记录为即将预约状态 
            // 0：正常 
            // 1：即将放号 
            // -1：当天已停止挂号
            if (i == len - 1 && page == iPage.getPages()) {
                bookingScheduleRuleVo.setStatus(1);
            }
            else {
                bookingScheduleRuleVo.setStatus(0);
            }

            // 当天预约如果过了停号时间， 不能预约
            if (i == 0 && page == 1) {
                final DateTime stopTime = this.getDateTime(new Date(), bookingRule.getStopTime());
                if (stopTime.isBeforeNow()) {
                    bookingScheduleRuleVo.setStatus(-1);
                }
            }
            bookingScheduleRuleVoList.add(bookingScheduleRuleVo);
        }

        // 科室信息
        final Department department = departmentService.getDepartment(hoscode, depcode);

        // 可预约日期规则数据
        Map<String, Object> result = new HashMap<>();
        result.put("bookingScheduleList", bookingScheduleRuleVoList);
        result.put("total", iPage.getTotal());

        // 基础数据
        Map<String, String> baseMap = new HashMap<>();
        baseMap.put("hosname", hospitalService.getHospName(hoscode));
        baseMap.put("bigname", department.getBigname());
        baseMap.put("depname", department.getDepname());
        baseMap.put("workDateString", new DateTime().toString("yyyy年MM月"));
        baseMap.put("releaseTime", bookingRule.getReleaseTime());
        baseMap.put("stopTime", bookingRule.getStopTime());
        result.put("baseMap", baseMap);

        return result;
    }

    /**
     * 根据id获取排班
     *
     * @param id 编号
     * @return {@link Schedule}
     */
    @Override
    public Schedule getById(String id) {
        final Schedule schedule = scheduleRepository
                .findById(id)
                .orElse(null);
        if (schedule == null) {
            return null;
        }
        return this.packageSchedule(schedule);
    }

    /**
     * 获取预约订单 VO
     *
     * @param scheduleId 预约 ID
     * @return {@link ScheduleOrderVo}
     */
    @Override
    public ScheduleOrderVo getScheduleOrderVo(String scheduleId) {
        final Schedule schedule = this.getById(scheduleId);
        if (schedule == null) {
            throw new YyghException(ResultCodeEnum.PARAM_ERROR);
        }
        final Hospital hospital = hospitalService.getByHoscode(schedule.getHoscode());
        if (hospital == null) {
            throw new YyghException(ResultCodeEnum.PARAM_ERROR);
        }
        final BookingRule bookingRule = hospital.getBookingRule();
        if (bookingRule == null) {
            throw new YyghException(ResultCodeEnum.PARAM_ERROR);
        }

        ScheduleOrderVo scheduleOrderVo = new ScheduleOrderVo();
        scheduleOrderVo.setHoscode(schedule.getHoscode());
        scheduleOrderVo.setHosname(hospital.getHosname());
        scheduleOrderVo.setDepcode(schedule.getDepcode());
        scheduleOrderVo.setDepname(departmentService.getDepName(schedule.getHoscode(), schedule.getDepcode()));
        scheduleOrderVo.setHosScheduleId(schedule.getHosScheduleId());
        scheduleOrderVo.setAvailableNumber(schedule.getAvailableNumber());
        scheduleOrderVo.setTitle(schedule.getTitle());
        scheduleOrderVo.setReserveDate(schedule.getWorkDate());
        scheduleOrderVo.setReserveTime(schedule.getWorkTime());
        scheduleOrderVo.setAmount(schedule.getAmount());

        // 退号截止天数（如：就诊前一天为-1，当天为0）
        scheduleOrderVo.setQuitTime(this
                .getDateTime(new DateTime(schedule.getWorkDate())
                        .plusDays(bookingRule.getQuitDay())
                        .toDate(), bookingRule.getQuitTime())
                .toDate());
        // 预约开始时间
        scheduleOrderVo.setStartTime(this
                .getDateTime(new Date(), bookingRule.getReleaseTime())
                .toDate());
        // 预约截止时间
        scheduleOrderVo.setEndTime(this
                .getDateTime(new DateTime()
                        .plusDays(bookingRule.getCycle())
                        .toDate(), bookingRule.getStopTime())
                .toDate());
        // 当天停止挂号时间
        scheduleOrderVo.setStopTime(this
                .getDateTime(new Date(), bookingRule.getStopTime())
                .toDate());

        return scheduleOrderVo;
    }

    /**
     * 修改排班
     *
     * @param schedule 排班
     */
    @Override
    public void update(Schedule schedule) {
        schedule.setUpdateTime(new Date());
        // 主键一致就是更新
        scheduleRepository.save(schedule);
    }

    /**
     * 获取可预约日志分页数据
     *
     * @param page        页码
     * @param limit       页面大小
     * @param bookingRule 预约规则
     * @return {@link IPage}<{@link Date}>
     */
    private IPage<Date> getListDate(int page, int limit, BookingRule bookingRule) {
        // 获取当天放号时间
        final DateTime releaseTime = this.getDateTime(new Date(), bookingRule.getReleaseTime());
        // 获取预约周期
        Integer cycle = bookingRule.getCycle();
        if (releaseTime.isBeforeNow()) {
            cycle++;
        }

        // 获取可预约所有日期，最后一天显示即将放号
        List<Date> dateList = new ArrayList<>();
        for (int i = 0; i < cycle; i++) {
            final DateTime curDateTime = releaseTime.plusDays(i);
            final String dateString = curDateTime.toString("yyyy-MM-dd");
            dateList.add(new DateTime(dateString).toDate());
        }

        // 因为预约周期不同的，每页显示日期最多7天数据，超过7天分页
        List<Date> pageDateList = new ArrayList<>();
        int start = (page - 1) * limit;
        int end = page * limit;
        // 如果可以显示数据小于7，直接显示
        if (end > dateList.size()) {
            end = dateList.size();
        }
        for (int i = start; i < end; i++) {
            pageDateList.add(dateList.get(i));
        }
        // 如果可以显示数据大于7，进行分页
        IPage<Date> iPage = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, 7, dateList.size());
        iPage.setRecords(pageDateList);
        return iPage;
    }

    /**
     * 将Date日期（yyyy-MM-dd HH:mm）转换为DateTime
     *
     * @param date       日期
     * @param timeString 时间字符串
     * @return {@link DateTime}
     */
    private DateTime getDateTime(Date date, String timeString) {
        final String dateTimeString = new DateTime(date).toString("yyyy-MM-dd") + " " + timeString;
        return DateTimeFormat
                .forPattern("yyyy-MM-dd HH:mm")
                .parseDateTime(dateTimeString);
    }

    /**
     * 封装排班详情其他值：医院名称、科室名称、日期对应星期
     *
     * @param schedule 附表
     */
    private Schedule packageSchedule(Schedule schedule) {
        final String hoscode = schedule.getHoscode();
        final String depcode = schedule.getDepcode();
        final Date workDate = schedule.getWorkDate();
        // 设置医院名称
        schedule
                .getParam()
                .put("hosname", hospitalService.getHospName(hoscode));
        // 设置科室名称
        schedule
                .getParam()
                .put("depname", departmentService.getDepName(hoscode, depcode));
        // 设置日期对应星期
        schedule
                .getParam()
                .put("dayOfWeek", this.getDayOfWeek(new DateTime(workDate)));

        return schedule;
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
