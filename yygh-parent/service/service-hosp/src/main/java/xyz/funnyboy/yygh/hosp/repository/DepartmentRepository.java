package xyz.funnyboy.yygh.hosp.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import xyz.funnyboy.yygh.model.hosp.Department;

/**
 * DepartmentRepository
 *
 * @author VectorX
 * @version V1.0
 * @date 2024-01-08 08:50:09
 */
@Repository
public interface DepartmentRepository extends MongoRepository<Department, String>
{
    /**
     * 通过 医院编号 和 科室编号 获取部门
     *
     * @param hoscode 医院编号
     * @param depcode 科室编号
     */
    Department getDepartmentByHoscodeAndDepcode(String hoscode, String depcode);
}
