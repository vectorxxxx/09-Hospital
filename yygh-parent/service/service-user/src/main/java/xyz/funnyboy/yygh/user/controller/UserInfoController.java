package xyz.funnyboy.yygh.user.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.funnyboy.yygh.common.result.Result;
import xyz.funnyboy.yygh.model.user.UserInfo;
import xyz.funnyboy.yygh.user.service.UserInfoService;
import xyz.funnyboy.yygh.vo.user.UserInfoQueryVo;

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
@RequestMapping("/admin/user/")
public class UserInfoController
{
    @Autowired
    private UserInfoService userInfoService;

    @ApiOperation(value = "用户列表（条件查询带分页）")
    @GetMapping("{page}/{limit}")
    public Result list(
            @ApiParam(name = "page",
                      value = "分页参数",
                      required = true)
            @PathVariable
                    Long page,

            @ApiParam(name = "limit",
                      value = "分页参数",
                      required = true)
            @PathVariable
                    Long limit,

            @ApiParam(name = "userInfoQueryVo",
                      value = "用户信息查询对象",
                      required = true)
                    UserInfoQueryVo userInfoQueryVo) {
        Page<UserInfo> pageParam = new Page<>(page, limit);
        final IPage<UserInfo> pages = userInfoService.selectPage(pageParam, userInfoQueryVo);
        return Result.ok(pages);
    }

    @ApiOperation(value = "用户锁定")
    @PostMapping("lock/{id}/{status}")
    public Result lock(
            @ApiParam(name = "id",
                      value = "用户ID",
                      required = true)
            @PathVariable
                    Long id,

            @ApiParam(name = "status",
                      value = "锁定状态（0：锁定 1：正常）",
                      required = true)
            @PathVariable
                    Integer status) {
        userInfoService.lock(id, status);
        return Result.ok();
    }

    @ApiOperation(value = "用户详情")
    @GetMapping("show/{id}")
    public Result show(
            @ApiParam(name = "id",
                      value = "用户ID",
                      required = true)
            @PathVariable
                    Long id) {
        final Map<String, Object> map = userInfoService.show(id);
        return Result.ok(map);
    }

    @ApiOperation(value = "认证审批")
    @PostMapping("approve/{userId}/{authStatus}")
    public Result approve(
            @ApiParam(name = "userId",
                      value = "用户ID",
                      required = true)
            @PathVariable
                    Long userId,

            @ApiParam(name = "authStatus",
                      value = "认证状态（-1：不通过 2：通过）",
                      required = true)
            @PathVariable
                    Integer authStatus) {
        userInfoService.approve(userId, authStatus);
        return Result.ok();
    }

}
