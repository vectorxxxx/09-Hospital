package xyz.funnyboy.yygh.hosp.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import xyz.funnyboy.yygh.common.result.Result;
import xyz.funnyboy.yygh.hosp.service.HospitalService;
import xyz.funnyboy.yygh.model.hosp.Hospital;
import xyz.funnyboy.yygh.vo.hosp.HospitalQueryVo;

import java.util.Map;

/**
 * HospitalController
 *
 * @author VectorX
 * @version V1.0
 * @date 2024-01-08 20:00:12
 */
@Api(tags = "医院管理")
@RestController
@RequestMapping("/admin/hosp/hospital")
// @CrossOrigin
public class HospitalController
{
    @Autowired
    private HospitalService hospitalService;

    @ApiOperation(value = "医院列表(条件查询分页)")
    @GetMapping("list/{page}/{limit}")
    public Result listHosp(
            @ApiParam(name = "page",
                      value = "页码",
                      required = true)
            @PathVariable
                    Integer page,

            @ApiParam(name = "limit",
                      value = "页面大小",
                      required = true)
            @PathVariable
                    Integer limit,

            @ApiParam(name = "searchObj",
                      value = "查询条件")
                    HospitalQueryVo hospitalQueryVo) {
        final Page<Hospital> pageModel = hospitalService.selectHospPage(page, limit, hospitalQueryVo);
        return Result.ok(pageModel);
    }

    @ApiOperation(value = "更新医院上线状态")
    @PutMapping("updateHospStatus/{id}/{status}")
    public Result updateHospStatus(
            @ApiParam(name = "id",
                      value = "医院ID",
                      required = true)
            @PathVariable
                    String id,

            @ApiParam(name = "status",
                      value = "状态",
                      required = true)
            @PathVariable
                    Integer status) {
        hospitalService.updateStatus(id, status);
        return Result.ok();
    }

    @ApiOperation(value = "获取医院详情")
    @GetMapping("showHospDetail/{id}")
    public Result showHospDetail(
            @ApiParam(name = "id",
                      value = "医院ID",
                      required = true)
            @PathVariable
                    String id) {
        final Map<String, Object> map = hospitalService.getHospById(id);
        return Result.ok(map);
    }
}
