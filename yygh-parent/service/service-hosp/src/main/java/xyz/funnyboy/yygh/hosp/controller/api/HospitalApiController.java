package xyz.funnyboy.yygh.hosp.controller.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.funnyboy.yygh.common.result.Result;
import xyz.funnyboy.yygh.hosp.service.HospitalService;
import xyz.funnyboy.yygh.model.hosp.Hospital;
import xyz.funnyboy.yygh.vo.hosp.HospitalQueryVo;

import java.util.List;

/**
 * 医院管理接口
 *
 * @author VectorX
 * @version V1.0
 * @date 2024-01-12 21:01:11
 */
@Api(tags = "医院管理接口")
@RestController
@RequestMapping("/api/hosp/hospital")
public class HospitalApiController
{
    @Autowired
    private HospitalService hospitalService;

    @ApiOperation(value = "获取分页列表")
    @GetMapping("{page}/{limit}")
    public Result index(
            @ApiParam(name = "page",
                      value = "当前页码",
                      required = true)
            @PathVariable
                    Integer page,

            @ApiParam(name = "limit",
                      value = "每页记录数",
                      required = true)
            @PathVariable
                    Integer limit,

            @ApiParam(name = "searchObj",
                      value = "查询对象",
                      required = false)
                    HospitalQueryVo searchObj) {
        final Page<Hospital> hospitals = hospitalService.selectHospPage(page, limit, searchObj);
        return Result.ok(hospitals);
    }

    @ApiOperation(value = "根据医院名称获取医院列表")
    @GetMapping("findByHosname/{hosname}")
    public Result findByHosname(
            @ApiParam(name = "hosname",
                      value = "医院名称",
                      required = true)
            @PathVariable
                    String hosname) {
        final List<Hospital> hospitalList = hospitalService.findByHosname(hosname);
        return Result.ok(hospitalList);
    }
}
