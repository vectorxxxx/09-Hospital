package xyz.funnyboy.yygh.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import xyz.funnyboy.yygh.model.user.UserInfo;
import xyz.funnyboy.yygh.vo.user.LoginVo;
import xyz.funnyboy.yygh.vo.user.UserAuthVo;
import xyz.funnyboy.yygh.vo.user.UserInfoQueryVo;

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

    /**
     * 用户身份验证
     *
     * @param userId     用户 ID
     * @param userAuthVo 用户身份验证 vo
     */
    void userAuth(Long userId, UserAuthVo userAuthVo);

    /**
     * 用户列表（条件查询带分页）
     *
     * @param pageParam       页面参数
     * @param userInfoQueryVo 用户信息查询 vo
     * @return {@link IPage}<{@link UserInfo}>
     */
    IPage<UserInfo> selectPage(Page<UserInfo> pageParam, UserInfoQueryVo userInfoQueryVo);

    /**
     * 用户锁定
     *
     * @param userId 用户 ID
     * @param status 状态
     */
    void lock(Long userId, Integer status);

    /**
     * 获取详情
     *
     * @param userId 用户 ID
     * @return {@link Map}<{@link String},{@link Object}>
     */
    Map<String, Object> show(Long userId);

    /**
     * 认证审批
     *
     * @param userId     用户 ID
     * @param authStatus 状态
     */
    void approve(Long userId, Integer authStatus);
}
