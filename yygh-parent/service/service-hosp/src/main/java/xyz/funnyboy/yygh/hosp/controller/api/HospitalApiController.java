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
import xyz.funnyboy.yygh.hosp.service.DepartmentService;
import xyz.funnyboy.yygh.hosp.service.HospitalService;
import xyz.funnyboy.yygh.hosp.service.HospitalSetService;
import xyz.funnyboy.yygh.hosp.service.ScheduleService;
import xyz.funnyboy.yygh.model.hosp.Hospital;
import xyz.funnyboy.yygh.model.hosp.Schedule;
import xyz.funnyboy.yygh.vo.hosp.DepartmentVo;
import xyz.funnyboy.yygh.vo.hosp.HospitalQueryVo;
import xyz.funnyboy.yygh.vo.hosp.ScheduleOrderVo;
import xyz.funnyboy.yygh.vo.order.SignInfoVo;

import java.util.List;
import java.util.Map;

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

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private HospitalSetService hospitalSetService;

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

    @ApiOperation(value = "查询医院编号所有科室列表")
    @GetMapping("department/{hoscode}")
    public Result getDeptList(
            @ApiParam(name = "hoscode",
                      value = "医院编号",
                      required = true)
            @PathVariable
                    String hoscode) {
        final List<DepartmentVo> deptTree = departmentService.findDeptTree(hoscode);
        return Result.ok(deptTree);
    }

    @ApiOperation(value = "根据医院编号获取医院预约挂号详情")
    @GetMapping("findHospDetail/{hoscode}")
    public Result findHospDetail(
            @ApiParam(name = "hoscode",
                      value = "医院编号",
                      required = true)
            @PathVariable
                    String hoscode) {
        final Map<String, Object> map = hospitalService.item(hoscode);
        return Result.ok(map);
    }

    @ApiOperation(value = "获取可预约排班数据")
    @GetMapping("auth/getBookingScheduleRule/{page}/{limit}/{hoscode}/{depcode}")
    public Result getBookingScheduleRule(
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

            @ApiParam(name = "hoscode",
                      value = "医院编号",
                      required = true)
            @PathVariable
                    String hoscode,

            @ApiParam(name = "depcode",
                      value = "科室编号",
                      required = true)
            @PathVariable
                    String depcode) {
        final Map<String, Object> map = scheduleService.getBookingScheduleRule(page, limit, hoscode, depcode);
        return Result.ok(map);
    }

    @ApiOperation(value = "获取排班数据")
    @GetMapping("auth/findScheduleList/{hoscode}/{depcode}/{workDate}")
    public Result findScheduleList(
            @ApiParam(name = "hoscode",
                      value = "医院编号",
                      required = true)
            @PathVariable
                    String hoscode,

            @ApiParam(name = "depcode",
                      value = "科室编号",
                      required = true)
            @PathVariable
                    String depcode,

            @ApiParam(name = "workDate",
                      value = "排班日期",
                      required = true)
            @PathVariable
                    String workDate) {
        final List<Schedule> scheduleList = scheduleService.getDetailSchedule(hoscode, depcode, workDate);
        return Result.ok(scheduleList);
    }

    @ApiOperation(value = "根据排班ID获取排班数据")
    @GetMapping("getSchedule/{scheduleId}")
    public Result getSchedule(
            @ApiParam(name = "scheduleId",
                      value = "排班ID",
                      required = true)
            @PathVariable
                    String scheduleId) {
        return Result.ok(scheduleService.getById(scheduleId));
    }

    @ApiOperation(value = "根据排班ID获取预约下单数据")
    @GetMapping("inner/getScheduleOrderVo/{scheduleId}")
    public ScheduleOrderVo getScheduleOrderVo(
            @ApiParam(name = "scheduleId",
                      value = "排班ID",
                      required = true)
            @PathVariable("scheduleId")
                    String scheduleId) {
        return scheduleService.getScheduleOrderVo(scheduleId);
    }

    @ApiOperation(value = "获取医院签名信息")
    @GetMapping("inner/getSignInfo/{hoscode}")
    public SignInfoVo getSignInfo(
            @ApiParam(name = "hoscode",
                      value = "医院编号",
                      required = true)
            @PathVariable("hoscode")
                    String hoscode) {
        return hospitalSetService.getSignInfo(hoscode);
    }
}
