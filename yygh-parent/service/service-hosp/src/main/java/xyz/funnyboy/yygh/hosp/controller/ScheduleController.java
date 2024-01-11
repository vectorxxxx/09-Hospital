package xyz.funnyboy.yygh.hosp.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.funnyboy.yygh.common.result.Result;
import xyz.funnyboy.yygh.hosp.service.ScheduleService;
import xyz.funnyboy.yygh.model.hosp.Schedule;

import java.util.List;
import java.util.Map;

/**
 * ScheduleController
 *
 * @author VectorX
 * @version V1.0
 * @date 2024-01-11 21:04:54
 */
@Api(tags = "排班接口")
@RestController
@RequestMapping("/admin/hosp/schedule")
@CrossOrigin
public class ScheduleController
{
    @Autowired
    private ScheduleService scheduleService;

    @ApiOperation(value = "查询排班规则数据")
    @GetMapping("getScheduleRule/{page}/{limit}/{hoscode}/{depcode}")
    public Result getScheduleRule(
            @ApiParam(name = "page",
                      value = "当前页码",
                      required = true)
            @PathVariable
                    Long page,

            @ApiParam(name = "limit",
                      value = "每页记录数",
                      required = true)
            @PathVariable
                    Long limit,

            @ApiParam(name = "hoscode",
                      value = "医院code",
                      required = true)
            @PathVariable
                    String hoscode,

            @ApiParam(name = "depcode",
                      value = "科室code",
                      required = true)
            @PathVariable
                    String depcode) {
        final Map<String, Object> ruleSchedule = scheduleService.getRuleSchedule(page, limit, hoscode, depcode);
        return Result.ok(ruleSchedule);
    }

    @ApiOperation(value = "查询排班详细信息")
    @GetMapping("getScheduleDetail/{hoscode}/{depcode}/{workDate}")
    public Result getScheduleDetail(
            @ApiParam(name = "hoscode",
                      value = "医院code",
                      required = true)
            @PathVariable
                    String hoscode,

            @ApiParam(name = "depcode",
                      value = "科室code",
                      required = true)
            @PathVariable
                    String depcode,

            @ApiParam(name = "workDate",
                      value = "工作日期",
                      required = true)
            @PathVariable
                    String workDate) {
        final List<Schedule> scheduleList = scheduleService.getDetailSchedule(hoscode, depcode, workDate);
        return Result.ok(scheduleList);
    }
}
