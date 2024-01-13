package xyz.funnyboy.yygh.hosp.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import xyz.funnyboy.yygh.model.hosp.Hospital;

import java.util.List;

/**
 * HospitalRepository
 *
 * @author VectorX
 * @version V1.0
 * @date 2024-01-07 22:23:48
 */
@Repository
public interface HospitalRepository extends MongoRepository<Hospital, String>
{
    /**
     * 通过医院编号获得医院信息
     *
     * @param hoscode 医院编号
     * @return {@link Hospital}
     */
    Hospital getHospitalByHoscode(String hoscode);

    /**
     * 按 医院名称 查找医院
     *
     * @param hosname 医院名称
     * @return {@link List}<{@link Hospital}>
     */
    List<Hospital> findHospitalsByHosnameLike(String hosname);
}
