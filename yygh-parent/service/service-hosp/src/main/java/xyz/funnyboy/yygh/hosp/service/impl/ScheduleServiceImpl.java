package xyz.funnyboy.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import xyz.funnyboy.yygh.hosp.repository.ScheduleRepository;
import xyz.funnyboy.yygh.hosp.service.ScheduleService;
import xyz.funnyboy.yygh.model.hosp.Schedule;
import xyz.funnyboy.yygh.vo.hosp.ScheduleQueryVo;

import java.util.Date;
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

}
