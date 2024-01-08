package xyz.funnyboy.yygh.hosp.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import xyz.funnyboy.yygh.model.hosp.Schedule;

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
     * 通过 HOScode 和 HOS 计划 ID 获取时间表
     *
     * @param hoscode       霍斯科
     * @param hosScheduleId HOS 计划 ID
     */
    Schedule getScheduleByHoscodeAndHosScheduleId(String hoscode, String hosScheduleId);
}
