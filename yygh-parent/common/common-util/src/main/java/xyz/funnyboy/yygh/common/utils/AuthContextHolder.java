package xyz.funnyboy.yygh.common.utils;

import xyz.funnyboy.yygh.common.helper.JwtHelper;

import javax.servlet.http.HttpServletRequest;

/**
 * 获取当前用户信息工具类
 *
 * @author VectorX
 * @version 1.0.0
 * @date 2024/01/06
 */
public class AuthContextHolder
{

    /**
     * 获取用户 ID
     *
     * @param request 请求
     * @return {@link Long}
     */
    public static Long getUserId(HttpServletRequest request) {
        //从header获取token
        String token = request.getHeader("token");
        //jwt从token获取userid
        return JwtHelper.getUserId(token);
    }

    /**
     * 获取用户名
     *
     * @param request 请求
     * @return {@link String}
     */
    public static String getUserName(HttpServletRequest request) {
        //从header获取token
        String token = request.getHeader("token");
        //jwt从token获取userid
        return JwtHelper.getUserName(token);
    }
}
