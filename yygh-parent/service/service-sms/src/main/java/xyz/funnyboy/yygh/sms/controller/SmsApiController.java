package xyz.funnyboy.yygh.sms.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.funnyboy.yygh.common.result.Result;
import xyz.funnyboy.yygh.sms.service.SmsService;
import xyz.funnyboy.yygh.sms.utils.RandomUtil;

import java.util.concurrent.TimeUnit;

/**
 * @author VectorX
 * @version V1.0
 * @date 2024-01-13 15:40:35
 */
@Api(tags = "短信API接口")
@RestController
@RequestMapping("/api/sms")
public class SmsApiController
{
    @Autowired
    private SmsService smsService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @ApiOperation(value = "发送短信验证码")
    @GetMapping("/send/{phone}")
    public Result sendCode(
            @ApiParam(name = "phone",
                      value = "手机号码",
                      required = true)
            @PathVariable
                    String phone) {
        // 先从redis获取下验证码，有则直接返回成功
        String code = redisTemplate
                .opsForValue()
                .get(phone);
        if (!StringUtils.isEmpty(code)) {
            return Result.ok();
        }

        // redis取不到验证码
        // 生成验证码
        code = RandomUtil.getSixBitRandom();
        // 发送短信
        final boolean isSend = smsService.send(phone, code);
        if (isSend) {
            redisTemplate
                    .opsForValue()
                    .set(phone, code, 5, TimeUnit.MINUTES);
            return Result.ok();
        }
        else {
            return Result
                    .fail()
                    .message("发送验证码失败");
        }
    }

}
