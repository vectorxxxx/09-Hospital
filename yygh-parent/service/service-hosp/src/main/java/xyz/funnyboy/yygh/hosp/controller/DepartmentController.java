package xyz.funnyboy.yygh.hosp.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.funnyboy.yygh.common.result.Result;
import xyz.funnyboy.yygh.hosp.service.DepartmentService;
import xyz.funnyboy.yygh.vo.hosp.DepartmentVo;

import java.util.List;

/**
 * DepartmentController
 *
 * @author VectorX
 * @version V1.0
 * @date 2024-01-10 23:48:50
 */
@Api(tags = "科室管理")
@RestController
@RequestMapping("/admin/hosp/department")
// @CrossOrigin
public class DepartmentController
{
    @Autowired
    private DepartmentService departmentService;

    @ApiOperation(value = "查询医院所有科室列表")
    @GetMapping("/getDeptList/{hoscode}")
    public Result getDeptList(
            @ApiParam(name = "hoscode",
                      value = "医院编号",
                      required = true)
            @PathVariable
                    String hoscode) {
        final List<DepartmentVo> deptTree = departmentService.findDeptTree(hoscode);
        return Result.ok(deptTree);
    }
}
