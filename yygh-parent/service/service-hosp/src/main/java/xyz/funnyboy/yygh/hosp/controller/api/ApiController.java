package xyz.funnyboy.yygh.hosp.controller.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.funnyboy.yygh.common.exception.YyghException;
import xyz.funnyboy.yygh.common.helper.HttpRequestHelper;
import xyz.funnyboy.yygh.common.result.Result;
import xyz.funnyboy.yygh.common.result.ResultCodeEnum;
import xyz.funnyboy.yygh.common.utils.MD5;
import xyz.funnyboy.yygh.hosp.service.HospitalService;
import xyz.funnyboy.yygh.hosp.service.HospitalSetService;
import xyz.funnyboy.yygh.model.hosp.Hospital;

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
@CrossOrigin
public class ApiController
{
    @Autowired
    private HospitalService hospitalService;

    @Autowired
    private HospitalSetService hospitalSetService;

    @ApiOperation(value = "上传医院")
    @PostMapping("saveHospital")
    public Result saveHospital(HttpServletRequest request) {
        // 获取传递过来的请求参数
        final Map<String, Object> paramMap = HttpRequestHelper.switchMap(request.getParameterMap());

        // 验签
        final String hoscode = (String) paramMap.get("hoscode");
        final String signKey = hospitalSetService.getSignKey(hoscode);
        final String encrypt = MD5.encrypt(signKey);
        if (!HttpRequestHelper.isSignEquals(paramMap, encrypt)) {
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }

        // 传输过程中“+”转换为了“ ”，因此我们要转换回来
        final String logoData = (String) paramMap.get("logoData");
        paramMap.put("logoData", logoData.replaceAll(" ", "+"));

        // 保存
        hospitalService.save(paramMap);
        return Result.ok();
    }

    @ApiOperation(value = "查询医院")
    @GetMapping("hospital/show")
    public Result getHospital(HttpServletRequest request) {
        // 获取传递过来的请求参数
        final Map<String, Object> paramMap = HttpRequestHelper.switchMap(request.getParameterMap());

        // 验签
        final String hoscode = (String) paramMap.get("hoscode");
        final String signKey = hospitalSetService.getSignKey(hoscode);
        final String encrypt = MD5.encrypt(signKey);
        if (!HttpRequestHelper.isSignEquals(paramMap, encrypt)) {
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }

        final Hospital hospital = hospitalService.getByHoscode(hoscode);
        return Result.ok(hospital);
    }
}
