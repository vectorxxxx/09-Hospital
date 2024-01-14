package xyz.funnyboy.yygh.user.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.funnyboy.yygh.common.result.Result;
import xyz.funnyboy.yygh.common.utils.AuthContextHolder;
import xyz.funnyboy.yygh.model.user.Patient;
import xyz.funnyboy.yygh.user.service.PatientService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author VectorX
 * @version V1.0
 * @date 2024-01-14 21:38:32
 */
@Api(tags = "就诊人员管理接口")
@RestController
@RequestMapping("/api/user/patient")
public class PatientApiController
{
    @Autowired
    private PatientService patientService;

    @ApiOperation(value = "获取就诊人列表")
    @GetMapping("/auth/findAll")
    public Result findAll(HttpServletRequest request) {
        final Long userId = AuthContextHolder.getUserId(request);
        final List<Patient> patientList = patientService.findAllUserId(userId);
        return Result.ok(patientList);
    }

    @ApiOperation(value = "添加就诊人")
    @PostMapping("/auth/save")
    public Result save(
            @ApiParam(name = "patient",
                      value = "就诊人信息",
                      required = true)
            @RequestBody
                    Patient patient, HttpServletRequest request) {
        final Long userId = AuthContextHolder.getUserId(request);
        patient.setUserId(userId);
        patientService.save(patient);
        return Result.ok();
    }

    @ApiOperation(value = "根据ID获取就诊人信息")
    @GetMapping("/auth/get/{id}")
    public Result get(
            @ApiParam(value = "就诊人ID",
                      name = "id",
                      required = true)
            @PathVariable
                    Long id, HttpServletRequest request) {
        final Patient patient = patientService.getPatientId(id);
        return Result.ok(patient);
    }

    @ApiOperation(value = "更新就诊人信息")
    @PutMapping("/auth/update")
    public Result update(
            @ApiParam(name = "patient",
                      value = "就诊人信息",
                      required = true)
            @RequestBody
                    Patient patient, HttpServletRequest request) {
        final Long userId = AuthContextHolder.getUserId(request);
        patient.setUserId(userId);
        patientService.updateById(patient);
        return Result.ok();
    }

    @ApiOperation(value = "删除就诊人信息")
    @DeleteMapping("/auth/remove/{id}")
    public Result delete(
            @ApiParam(value = "就诊人ID",
                      name = "id",
                      required = true)
            @PathVariable
                    Long id) {
        patientService.removeById(id);
        return Result.ok();
    }
}
