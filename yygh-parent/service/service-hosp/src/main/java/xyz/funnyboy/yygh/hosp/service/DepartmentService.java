package xyz.funnyboy.yygh.hosp.service;

import org.springframework.data.domain.Page;
import xyz.funnyboy.yygh.model.hosp.Department;
import xyz.funnyboy.yygh.vo.hosp.DepartmentQueryVo;

import java.util.Map;

/**
 * DepartmentService
 *
 * @author VectorX
 * @version V1.0
 * @date 2024-01-08 08:50:48
 */
public interface DepartmentService
{
    /**
     * 上传科室
     *
     * @param paramMap 参数映射
     */
    void save(Map<String, Object> paramMap);

    /**
     * 查找科室
     *
     * @param page              页数
     * @param limit             每页大小
     * @param departmentQueryVo 部门查询VO
     * @return {@link Page}<{@link Department}>
     */
    Page<Department> findPageDepartment(int page, int limit, DepartmentQueryVo departmentQueryVo);

    /**
     * 删除科室
     *
     * @param hoscode 医院编号
     * @param depcode depcode
     */
    void remove(String hoscode, String depcode);
}
