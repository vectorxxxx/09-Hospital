package xyz.funnyboy.yygh.hosp.service;

import org.springframework.data.domain.Page;
import xyz.funnyboy.yygh.model.hosp.Schedule;
import xyz.funnyboy.yygh.vo.hosp.ScheduleOrderVo;
import xyz.funnyboy.yygh.vo.hosp.ScheduleQueryVo;

import java.util.List;
import java.util.Map;

/**
 * ScheduleService
 *
 * @author VectorX
 * @version V1.0
 * @date 2024-01-08 11:04:49
 */
public interface ScheduleService
{
    /**
     * 上传排班
     *
     * @param paramMap 参数映射
     */
    void save(Map<String, Object> paramMap);

    /**
     * 分页查询排班
     *
     * @param page            当前页
     * @param limit           每页大小
     * @param scheduleQueryVo 排班查询 VO
     * @return {@link Page}<{@link Schedule}>
     */
    Page<Schedule> findPageSchedule(int page, int limit, ScheduleQueryVo scheduleQueryVo);

    /**
     * 删除排班
     *
     * @param hoscode       医院编号
     * @param hosScheduleId 排班编号
     */
    void remove(String hoscode, String hosScheduleId);

    /**
     * 获取规则计划
     *
     * @param page    页码
     * @param limit   每页记录
     * @param hoscode 医院编号
     * @param depcode 科室编号
     * @return {@link Map}<{@link String}, {@link Object}>
     */
    Map<String, Object> getRuleSchedule(long page, long limit, String hoscode, String depcode);

    /**
     * 获取排班详情列表
     *
     * @param hoscode  医院编号
     * @param depcode  科室编号
     * @param workDate 工作日期
     * @return {@link List}<{@link Schedule}>
     */
    List<Schedule> getDetailSchedule(String hoscode, String depcode, String workDate);

    /**
     * 获取可预约的排班数据
     *
     * @param page    页码
     * @param limit   页面大小
     * @param hoscode 医院编号
     * @param depcode 科室编号
     * @return {@link Map}<{@link String}, {@link Object}>
     */
    Map<String, Object> getBookingScheduleRule(int page, int limit, String hoscode, String depcode);

    /**
     * 根据id获取排班
     *
     * @param id 编号
     * @return {@link Schedule}
     */
    Schedule getById(String id);

    /**
     * 获取预约订单 VO
     *
     * @param scheduleId 预约 ID
     * @return {@link ScheduleOrderVo}
     */
    ScheduleOrderVo getScheduleOrderVo(String scheduleId);

    /**
     * 修改排班
     *
     * @param schedule 排班
     */
    void update(Schedule schedule);
}
