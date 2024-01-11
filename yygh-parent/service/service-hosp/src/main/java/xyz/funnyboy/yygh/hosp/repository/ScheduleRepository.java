package xyz.funnyboy.yygh.hosp.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import xyz.funnyboy.yygh.model.hosp.Schedule;

import java.util.Date;
import java.util.List;

/**
 * ScheduleRepository
 *
 * @author VectorX
 * @version V1.0
 * @date 2024-01-08 11:04:24
 */
@Repository
public interface ScheduleRepository extends MongoRepository<Schedule, String>
{
    /**
     * 通过 HOScode 和 HOS 计划 ID 获取 排班信息
     *
     * @param hoscode       霍斯科
     * @param hosScheduleId HOS 计划 ID
     */
    Schedule getScheduleByHoscodeAndHosScheduleId(String hoscode, String hosScheduleId);

    /**
     * 按 医院编号 和 科室编号 以及 工作日期 获取 排班列表
     *
     * @param hoscode  霍斯科
     * @param depcode  depcode
     * @param workDate 工作日期
     * @return {@link List}<{@link Schedule}>
     */
    List<Schedule> findScheduleByHoscodeAndDepcodeAndWorkDate(String hoscode, String depcode, Date workDate);
}
