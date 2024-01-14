package xyz.funnyboy.yygh.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import xyz.funnyboy.yygh.cmn.client.DictFeignClient;
import xyz.funnyboy.yygh.common.exception.YyghException;
import xyz.funnyboy.yygh.common.helper.JwtHelper;
import xyz.funnyboy.yygh.common.result.ResultCodeEnum;
import xyz.funnyboy.yygh.enums.AuthStatusEnum;
import xyz.funnyboy.yygh.enums.DictEnum;
import xyz.funnyboy.yygh.model.user.UserInfo;
import xyz.funnyboy.yygh.user.mapper.UserInfoMapper;
import xyz.funnyboy.yygh.user.service.PatientService;
import xyz.funnyboy.yygh.user.service.UserInfoService;
import xyz.funnyboy.yygh.vo.user.LoginVo;
import xyz.funnyboy.yygh.vo.user.UserAuthVo;
import xyz.funnyboy.yygh.vo.user.UserInfoQueryVo;

import java.util.HashMap;
import java.util.Map;

/**
 * UserInfoServiceImpl
 *
 * @author VectorX
 * @version V1.0
 * @date 2024-01-13 14:00:20
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService
{
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private PatientService patientService;

    @Autowired
    private DictFeignClient dictFeignClient;

    /**
     * 会员登录
     *
     * @param loginVo 登录 VO
     * @return {@link Map}<{@link String}, {@link Object}>
     */
    @Override
    public Map<String, Object> loginUser(LoginVo loginVo) {
        final String phone = loginVo.getPhone();
        final String code = loginVo.getCode();

        // 非空校验
        if (StringUtils.isEmpty(phone) || StringUtils.isEmpty(code)) {
            throw new YyghException(ResultCodeEnum.PARAM_ERROR);
        }

        // 验证码校验
        final String mobileCode = redisTemplate
                .opsForValue()
                .get(phone);
        if (!code.equals(mobileCode)) {
            throw new YyghException(ResultCodeEnum.CODE_ERROR);
        }

        // 绑定手机号码
        UserInfo userInfo = null;
        final String openid = loginVo.getOpenid();
        if (!StringUtils.isEmpty(openid)) {
            userInfo = baseMapper.selectOne(new LambdaQueryWrapper<UserInfo>().eq(UserInfo::getOpenid, openid));
            if (userInfo != null) {
                userInfo.setPhone(phone);
                baseMapper.updateById(userInfo);
            }
            else {
                throw new YyghException(ResultCodeEnum.DATA_ERROR);
            }
        }

        //如果userinfo为空，进行正常手机登录
        if (userInfo == null) {
            // 手机号是否被使用
            userInfo = baseMapper.selectOne(new LambdaQueryWrapper<UserInfo>().eq(UserInfo::getPhone, phone));
            if (userInfo == null) {
                userInfo = new UserInfo();
                userInfo.setName("");
                userInfo.setPhone(phone);
                userInfo.setStatus(1);
                baseMapper.insert(userInfo);
            }
        }

        // 校验账号状态
        if (userInfo.getStatus() == 0) {
            throw new YyghException(ResultCodeEnum.LOGIN_DISABLED_ERROR);
        }

        // 组装数据
        Map<String, Object> result = new HashMap<>();
        String name = userInfo.getName();
        if (StringUtils.isEmpty(name)) {
            name = userInfo.getNickName();
        }
        if (StringUtils.isEmpty(name)) {
            name = userInfo.getPhone();
        }
        result.put("name", name);
        final String token = JwtHelper.createToken(userInfo.getId(), name);
        result.put("token", token);
        return result;
    }

    /**
     * 通过 微信号 获取 用户信息
     *
     * @param openid 微信号
     * @return {@link UserInfo}
     */
    @Override
    public UserInfo getByOpenid(String openid) {
        return baseMapper.selectOne(new LambdaQueryWrapper<UserInfo>().eq(UserInfo::getOpenid, openid));
    }

    /**
     * 用户身份验证
     *
     * @param userId     用户 ID
     * @param userAuthVo 用户身份验证 vo
     */
    @Override
    public void userAuth(Long userId, UserAuthVo userAuthVo) {
        final UserInfo userInfo = baseMapper.selectById(userId);
        userInfo.setName(userAuthVo.getName());
        userInfo.setCertificatesNo(userAuthVo.getCertificatesNo());
        userInfo.setCertificatesType(userAuthVo.getCertificatesType());
        userInfo.setCertificatesUrl(userAuthVo.getCertificatesUrl());
        userInfo.setAuthStatus(AuthStatusEnum.AUTH_RUN.getStatus());
        baseMapper.updateById(userInfo);
    }

    /**
     * 用户列表（条件查询带分页）
     *
     * @param pageParam       页面参数
     * @param userInfoQueryVo 用户信息查询 vo
     * @return {@link IPage}<{@link UserInfo}>
     */
    @Override
    public IPage<UserInfo> selectPage(Page<UserInfo> pageParam, UserInfoQueryVo userInfoQueryVo) {
        final String name = userInfoQueryVo.getKeyword();
        final Integer status = userInfoQueryVo.getStatus();
        final Integer authStatus = userInfoQueryVo.getAuthStatus();
        final String createTimeBegin = userInfoQueryVo.getCreateTimeBegin();
        final String createTimeEnd = userInfoQueryVo.getCreateTimeEnd();

        // 查询条件
        final LambdaQueryWrapper<UserInfo> queryWrapper = new LambdaQueryWrapper<UserInfo>()
                .eq(!StringUtils.isEmpty(name), UserInfo::getName, name)
                .eq(!StringUtils.isEmpty(status), UserInfo::getStatus, status)
                .eq(!StringUtils.isEmpty(authStatus), UserInfo::getAuthStatus, authStatus)
                .ge(!StringUtils.isEmpty(createTimeBegin), UserInfo::getCreateTime, createTimeBegin)
                .le(!StringUtils.isEmpty(createTimeEnd), UserInfo::getCreateTime, createTimeEnd);

        final Page<UserInfo> pages = baseMapper.selectPage(pageParam, queryWrapper);
        pages
                .getRecords()
                .forEach(this::packageUserInfo);
        return pages;
    }

    /**
     * 用户锁定
     *
     * @param userId 用户 ID
     * @param status 状态
     */
    @Override
    public void lock(Long userId, Integer status) {
        if (status == 0 || status == 1) {
            final UserInfo userInfo = baseMapper.selectById(userId);
            userInfo.setStatus(status);
            baseMapper.updateById(userInfo);
        }
    }

    /**
     * 获取详情
     *
     * @param userId 用户 ID
     * @return {@link Map}<{@link String},{@link Object}>
     */
    @Override
    public Map<String, Object> show(Long userId) {
        Map<String, Object> result = new HashMap<>();
        result.put("userInfo", baseMapper.selectById(userId));
        result.put("patientList", patientService.findAllUserId(userId));
        return result;
    }

    /**
     * 认证审批
     *
     * @param userId     用户 ID
     * @param authStatus 状态
     */
    @Override
    public void approve(Long userId, Integer authStatus) {
        if (authStatus == 2 || authStatus == -1) {
            final UserInfo userInfo = baseMapper.selectById(userId);
            userInfo.setAuthStatus(authStatus);
            baseMapper.updateById(userInfo);
        }
    }

    /**
     * 编号变成对应值封装
     *
     * @param userInfo 用户信息
     * @return {@link UserInfo}
     */
    private UserInfo packageUserInfo(UserInfo userInfo) {
        if (!StringUtils.isEmpty(userInfo.getCertificatesType())) {
            userInfo
                    .getParam()
                    .put("certificatesTypeString", dictFeignClient.getName(DictEnum.CERTIFICATES_TYPE.getDictCode(), userInfo.getCertificatesType()));
        }

        if (!StringUtils.isEmpty(userInfo.getAuthStatus())) {
            userInfo
                    .getParam()
                    .put("authStatusString", AuthStatusEnum.getStatusNameByStatus(userInfo.getAuthStatus()));
        }

        if (!StringUtils.isEmpty(userInfo.getStatus())) {
            userInfo
                    .getParam()
                    .put("statsString", userInfo.getStatus() == 1 ?
                                        "正常" :
                                        "锁定");
        }
        return userInfo;
    }
}
