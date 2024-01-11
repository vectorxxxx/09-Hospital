package xyz.funnyboy.yygh.hosp.controller.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import xyz.funnyboy.yygh.common.exception.YyghException;
import xyz.funnyboy.yygh.common.helper.HttpRequestHelper;
import xyz.funnyboy.yygh.common.result.Result;
import xyz.funnyboy.yygh.common.result.ResultCodeEnum;
import xyz.funnyboy.yygh.common.utils.MD5;
import xyz.funnyboy.yygh.hosp.service.DepartmentService;
import xyz.funnyboy.yygh.hosp.service.HospitalService;
import xyz.funnyboy.yygh.hosp.service.HospitalSetService;
import xyz.funnyboy.yygh.hosp.service.ScheduleService;
import xyz.funnyboy.yygh.model.hosp.Department;
import xyz.funnyboy.yygh.model.hosp.Hospital;
import xyz.funnyboy.yygh.model.hosp.Schedule;
import xyz.funnyboy.yygh.vo.hosp.DepartmentQueryVo;
import xyz.funnyboy.yygh.vo.hosp.ScheduleQueryVo;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * ApiController
 *
 * @author VectorX
 * @version V1.0
 * @date 2024-01-07 22:25:40
 */
@Api(tags = "医院管理API接口")
@RestController
@RequestMapping("/api/hosp")
// @CrossOrigin
@Slf4j
public class ApiController
{
    @Autowired
    private HospitalService hospitalService;

    @Autowired
    private HospitalSetService hospitalSetService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private ScheduleService scheduleService;

    @ApiOperation(value = "上传医院")
    @PostMapping("saveHospital")
    public Result saveHospital(HttpServletRequest request) {
        // 获取传递过来的请求参数
        final Map<String, Object> paramMap = HttpRequestHelper.switchMap(request.getParameterMap());

        // 判空
        final String hoscode = (String) paramMap.get("hoscode");
        if (StringUtils.isEmpty(hoscode)) {
            throw new YyghException(ResultCodeEnum.PARAM_ERROR);
        }

        // 验签
        if (!isSignEquals(paramMap)) {
            log.error(ResultCodeEnum.SIGN_ERROR.getMessage());
            // throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }

        // 传输过程中“+”转换为了“ ”，因此我们要转换回来
        final String logoData = (String) paramMap.get("logoData");
        paramMap.put("logoData", logoData.replaceAll(" ", "+"));

        // 保存
        hospitalService.save(paramMap);
        return Result.ok();
    }

    /**
     * 验签
     *
     * @param paramMap 参数映射
     * @return boolean
     */
    private boolean isSignEquals(Map<String, Object> paramMap) {
        // 传递过来的 sign
        String sign = (String) paramMap.get("sign");

        // 查询数据库的 signKey
        final String hoscode = (String) paramMap.get("hoscode");
        final String signKey = hospitalSetService.getSignKey(hoscode);
        final String encryptSignKey = MD5.encrypt(signKey);

        // 与传递过来的 sign 进行对比
        return sign.equals(encryptSignKey);
    }

    @ApiOperation(value = "查询医院")
    @PostMapping("hospital/show")
    public Result getHospital(HttpServletRequest request) {
        // 获取传递过来的请求参数
        final Map<String, Object> paramMap = HttpRequestHelper.switchMap(request.getParameterMap());

        // 判空
        final String hoscode = (String) paramMap.get("hoscode");
        if (StringUtils.isEmpty(hoscode)) {
            throw new YyghException(ResultCodeEnum.PARAM_ERROR);
        }

        // 验签
        if (!isSignEquals(paramMap)) {
            log.error(ResultCodeEnum.SIGN_ERROR.getMessage());
            // throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }

        final Hospital hospital = hospitalService.getByHoscode(hoscode);
        return Result.ok(hospital);
    }

    @ApiOperation(value = "上传科室")
    @PostMapping("saveDepartment")
    public Result saveDepartment(HttpServletRequest request) {
        final Map<String, Object> paramMap = HttpRequestHelper.switchMap(request.getParameterMap());

        // 判空
        final String hoscode = (String) paramMap.get("hoscode");
        final String depcode = (String) paramMap.get("depcode");
        if (StringUtils.isEmpty(hoscode) || StringUtils.isEmpty(depcode)) {
            throw new YyghException(ResultCodeEnum.PARAM_ERROR);
        }

        // 验签
        if (!isSignEquals(paramMap)) {
            log.error(ResultCodeEnum.SIGN_ERROR.getMessage());
            // throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }

        departmentService.save(paramMap);
        return Result.ok();
    }

    @ApiOperation(value = "查询科室")
    @PostMapping("department/list")
    public Result getDepartment(HttpServletRequest request) {
        final Map<String, Object> paramMap = HttpRequestHelper.switchMap(request.getParameterMap());

        // 验签
        if (!isSignEquals(paramMap)) {
            log.error(ResultCodeEnum.SIGN_ERROR.getMessage());
            // throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }

        // 查询条件
        final int page = StringUtils.isEmpty(paramMap.get("page")) ?
                         1 :
                         Integer.parseInt(paramMap
                                 .get("page")
                                 .toString());
        final int limit = StringUtils.isEmpty(paramMap.get("limit")) ?
                          10 :
                          Integer.parseInt(paramMap
                                  .get("limit")
                                  .toString());
        DepartmentQueryVo departmentQueryVo = new DepartmentQueryVo();
        final String hoscode = (String) paramMap.get("hoscode");
        final String depcode = (String) paramMap.get("depcode");
        departmentQueryVo.setHoscode(hoscode);
        departmentQueryVo.setDepcode(depcode);

        // 查询科室
        final Page<Department> pageModel = departmentService.findPageDepartment(page, limit, departmentQueryVo);
        return Result.ok(pageModel);
    }

    @ApiOperation(value = "删除科室")
    @DeleteMapping("department/remove")
    public Result removeDepartment(HttpServletRequest request) {
        final Map<String, Object> paramMap = HttpRequestHelper.switchMap(request.getParameterMap());

        // 判空
        final String hoscode = (String) paramMap.get("hoscode");
        final String depcode = (String) paramMap.get("depcode");
        if (StringUtils.isEmpty(hoscode) || StringUtils.isEmpty(depcode)) {
            throw new YyghException(ResultCodeEnum.PARAM_ERROR);
        }

        // 验签
        if (!isSignEquals(paramMap)) {
            log.error(ResultCodeEnum.SIGN_ERROR.getMessage());
            // throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }

        // 删除
        departmentService.remove(hoscode, depcode);
        return Result.ok();
    }

    @ApiOperation(value = "上传排班")
    @PostMapping("saveSchedule")
    public Result saveSchedule(HttpServletRequest request) {
        final Map<String, Object> paramMap = HttpRequestHelper.switchMap(request.getParameterMap());

        // 判空
        final String hoscode = (String) paramMap.get("hoscode");
        final String hosScheduleId = (String) paramMap.get("hosScheduleId");
        if (StringUtils.isEmpty(hoscode) || StringUtils.isEmpty(hosScheduleId)) {
            throw new YyghException(ResultCodeEnum.PARAM_ERROR);
        }

        // 验签
        if (!isSignEquals(paramMap)) {
            log.error(ResultCodeEnum.SIGN_ERROR.getMessage());
            // throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }

        scheduleService.save(paramMap);
        return Result.ok();
    }

    @ApiOperation(value = "获取排班")
    @PostMapping("schedule/list")
    public Result list(HttpServletRequest request) {
        // 请求参数
        final Map<String, Object> paramMap = HttpRequestHelper.switchMap(request.getParameterMap());

        // 验签
        if (!isSignEquals(paramMap)) {
            log.error(ResultCodeEnum.SIGN_ERROR.getMessage());
            // throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }

        // 分页参数
        final int page = StringUtils.isEmpty(paramMap.get("page")) ?
                         1 :
                         Integer.parseInt((String) paramMap.get("page"));
        final int limit = StringUtils.isEmpty(paramMap.get("limit")) ?
                          10 :
                          Integer.parseInt((String) paramMap.get("limit"));

        // 查询条件
        final ScheduleQueryVo scheduleQueryVo = new ScheduleQueryVo();
        scheduleQueryVo.setHoscode((String) paramMap.get("hoscode"));
        scheduleQueryVo.setDepcode((String) paramMap.get("depcode"));

        final Page<Schedule> pageModel = scheduleService.findPageSchedule(page, limit, scheduleQueryVo);
        return Result.ok(pageModel);
    }

    @ApiOperation(value = "删除排班")
    @DeleteMapping("schedule/remove")
    public Result remove(HttpServletRequest request) {
        // 请求参数
        final Map<String, Object> paramMap = HttpRequestHelper.switchMap(request.getParameterMap());

        // 判空
        final String hoscode = (String) paramMap.get("hoscode");
        final String hosScheduleId = (String) paramMap.get("hosScheduleId");
        if (StringUtils.isEmpty(hoscode) || StringUtils.isEmpty(hosScheduleId)) {
            throw new YyghException(ResultCodeEnum.PARAM_ERROR);
        }

        // 验签
        if (!isSignEquals(paramMap)) {
            log.error(ResultCodeEnum.SIGN_ERROR.getMessage());
            // throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }

        scheduleService.remove(hoscode, hosScheduleId);
        return Result.ok();
    }
}

