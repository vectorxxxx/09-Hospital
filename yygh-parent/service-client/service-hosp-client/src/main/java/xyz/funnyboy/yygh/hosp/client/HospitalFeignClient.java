package xyz.funnyboy.yygh.hosp.client;

import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import xyz.funnyboy.yygh.vo.hosp.ScheduleOrderVo;
import xyz.funnyboy.yygh.vo.order.SignInfoVo;

/**
 * @author VectorX
 * @version V1.0
 * @date 2024-01-15 23:59:32
 */
@Component
@FeignClient(value = "service-hosp")
public interface HospitalFeignClient
{
    @GetMapping("/api/hosp/hospital/inner/getScheduleOrderVo/{scheduleId}")
    ScheduleOrderVo getScheduleOrderVo(
            @ApiParam(name = "scheduleId",
                      value = "排班ID",
                      required = true)
            @PathVariable("scheduleId")
                    String scheduleId);

    @GetMapping("/api/hosp/hospital/inner/getSignInfo/{hoscode}")
    SignInfoVo getSignInfo(
            @ApiParam(name = "hoscode",
                      value = "医院编号",
                      required = true)
            @PathVariable("hoscode")
                    String hoscode);
}
