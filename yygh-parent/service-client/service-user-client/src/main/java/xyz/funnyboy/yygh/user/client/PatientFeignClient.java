package xyz.funnyboy.yygh.user.client;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import xyz.funnyboy.yygh.model.user.Patient;

/**
 * @author VectorX
 * @version V1.0
 * @date 2024-01-15 23:34:13
 */
@Component
@FeignClient(value = "service-user")
public interface PatientFeignClient
{
    @ApiOperation(value = "获取就诊人")
    @GetMapping("/api/user/patient/inner/get/{id}")
    Patient getPatient(
            @ApiParam(value = "就诊人ID",
                      name = "id",
                      required = true)
            @PathVariable("id")
                    Long id);
}
