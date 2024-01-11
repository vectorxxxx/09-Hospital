package xyz.funnyboy.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import xyz.funnyboy.yygh.hosp.repository.DepartmentRepository;
import xyz.funnyboy.yygh.hosp.service.DepartmentService;
import xyz.funnyboy.yygh.model.hosp.Department;
import xyz.funnyboy.yygh.vo.hosp.DepartmentQueryVo;
import xyz.funnyboy.yygh.vo.hosp.DepartmentVo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * DepartmentServiceImpl
 *
 * @author VectorX
 * @version V1.0
 * @date 2024-01-08 08:51:56
 */
@Service
public class DepartmentServiceImpl implements DepartmentService
{
    @Autowired
    private DepartmentRepository departmentRepository;

    /**
     * 上传科室
     *
     * @param paramMap 参数映射
     */
    @Override
    public void save(Map<String, Object> paramMap) {
        Department department = JSONObject.parseObject(JSONObject.toJSONString(paramMap), Department.class);

        // 查询可是是否存在
        Department departmentByHoscodeAndDepcode = departmentRepository.getDepartmentByHoscodeAndDepcode(department.getHoscode(), department.getDepcode());

        // 存在则更新，不存在则新增
        if (departmentByHoscodeAndDepcode != null) {
            departmentByHoscodeAndDepcode.setUpdateTime(new Date());
            departmentByHoscodeAndDepcode.setIsDeleted(0);
            departmentRepository.save(departmentByHoscodeAndDepcode);
        }
        else {
            department.setCreateTime(new Date());
            department.setUpdateTime(new Date());
            department.setIsDeleted(0);
            departmentRepository.save(department);
        }
    }

    /**
     * 查找科室
     *
     * @param page              页数
     * @param limit             每页大小
     * @param departmentQueryVo 部门查询VO
     * @return {@link Page}<{@link Department}>
     */
    @Override
    public Page<Department> findPageDepartment(int page, int limit, DepartmentQueryVo departmentQueryVo) {
        final Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        final PageRequest pageRequest = PageRequest.of(page - 1, limit, sort);

        // 查询条件
        final Department department = new Department();
        BeanUtils.copyProperties(departmentQueryVo, department);
        department.setIsDeleted(0);

        // 创建匹配器
        final ExampleMatcher exampleMatcher = ExampleMatcher
                .matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true);
        final Example<Department> example = Example.of(department, exampleMatcher);

        return departmentRepository.findAll(example, pageRequest);
    }

    /**
     * 删除科室
     *
     * @param hoscode 医院编号
     * @param depcode depcode
     */
    @Override
    public void remove(String hoscode, String depcode) {
        final Department department = departmentRepository.getDepartmentByHoscodeAndDepcode(hoscode, depcode);
        if (department != null) {
            departmentRepository.deleteById(department.getId());
        }
    }

    /**
     * 根据医院编号，查询医院所有科室列表
     *
     * @param hoscode 医院编号
     * @return {@link List}<{@link DepartmentVo}>
     */
    @Override
    public List<DepartmentVo> findDeptTree(String hoscode) {
        List<DepartmentVo> result = new ArrayList<>();

        // 查询医院所有科室信息
        Department departmentQuery = new Department();
        departmentQuery.setHoscode(hoscode);
        final Example<Department> example = Example.of(departmentQuery);
        final List<Department> departmentList = departmentRepository.findAll(example);

        // 根据大科室编号 bigcode 分组，获取每个大科室的下级子科室
        final Map<String, List<Department>> departmentListMap = departmentList
                .stream()
                .collect(Collectors.groupingBy(Department::getBigcode));

        // 封装查询结果
        for (Map.Entry<String, List<Department>> entry : departmentListMap.entrySet()) {
            final String bigCode = entry.getKey();
            final List<Department> children = entry.getValue();

            // 封装大科室
            DepartmentVo departmentVo = new DepartmentVo();
            departmentVo.setDepcode(bigCode);
            departmentVo.setDepname(children
                    .get(0)
                    .getBigname());

            // 封装小科室
            List<DepartmentVo> childrenList = new ArrayList<>();
            for (Department child : children) {
                DepartmentVo childVo = new DepartmentVo();
                childVo.setDepcode(child.getDepcode());
                childVo.setDepname(child.getDepname());
                childrenList.add(childVo);
            }
            departmentVo.setChildren(childrenList);

            result.add(departmentVo);
        }

        return result;
    }

    /**
     * 获取科室名称
     *
     * @param hoscode 医院编号
     * @param depcode 科室编号
     * @return {@link String}
     */
    @Override
    public String getDepName(String hoscode, String depcode) {
        final Department department = departmentRepository.getDepartmentByHoscodeAndDepcode(hoscode, depcode);
        if (department != null) {
            return department.getDepname();
        }
        return "";
    }
}
