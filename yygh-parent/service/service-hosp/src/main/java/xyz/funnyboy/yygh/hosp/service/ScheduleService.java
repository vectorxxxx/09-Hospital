package xyz.funnyboy.yygh.hosp.service;

import org.springframework.data.domain.Page;
import xyz.funnyboy.yygh.model.hosp.Schedule;
import xyz.funnyboy.yygh.vo.hosp.ScheduleQueryVo;

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
}
