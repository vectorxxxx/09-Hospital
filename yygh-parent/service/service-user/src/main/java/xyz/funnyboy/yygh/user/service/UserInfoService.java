package xyz.funnyboy.yygh.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.funnyboy.yygh.model.user.UserInfo;
import xyz.funnyboy.yygh.vo.user.LoginVo;

import java.util.Map;

/**
 * UserInfoService
 *
 * @author VectorX
 * @version V1.0
 * @date 2024-01-13 13:59:47
 */
public interface UserInfoService extends IService<UserInfo>
{
    /**
     * 会员登录
     *
     * @param loginVo 登录 VO
     * @return {@link Map}<{@link String}, {@link Object}>
     */
    Map<String, Object> loginUser(LoginVo loginVo);

    /**
     * 通过 微信号 获取 用户信息
     *
     * @param openid 微信号
     * @return {@link UserInfo}
     */
    UserInfo getByOpenid(String openid);
}
