package xyz.funnyboy.yygh.user.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.funnyboy.yygh.common.result.Result;
import xyz.funnyboy.yygh.common.utils.IpUtil;
import xyz.funnyboy.yygh.user.service.UserInfoService;
import xyz.funnyboy.yygh.vo.user.LoginVo;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * UserInfoApiController
 *
 * @author VectorX
 * @version V1.0
 * @date 2024-01-13 13:54:33
 */
@Api(tags = "用户管理")
@RestController
@RequestMapping("/api/user/")
public class UserInfoApiController
{
    @Autowired
    private UserInfoService userInfoService;

    @ApiOperation(value = "会员登录")
    @PostMapping("/login")
    public Result login(
            @ApiParam(name = "loginVo",
                      value = "登录对象",
                      required = true)
            @RequestBody
                    LoginVo loginVo, HttpServletRequest request) {
        loginVo.setIp(IpUtil.getIpAddr(request));
        final Map<String, Object> info = userInfoService.loginUser(loginVo);
        return Result.ok(info);
    }
}
