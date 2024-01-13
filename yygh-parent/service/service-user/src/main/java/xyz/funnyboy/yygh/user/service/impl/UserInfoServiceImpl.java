package xyz.funnyboy.yygh.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import xyz.funnyboy.yygh.common.exception.YyghException;
import xyz.funnyboy.yygh.common.helper.JwtHelper;
import xyz.funnyboy.yygh.common.result.ResultCodeEnum;
import xyz.funnyboy.yygh.model.user.UserInfo;
import xyz.funnyboy.yygh.user.mapper.UserInfoMapper;
import xyz.funnyboy.yygh.user.service.UserInfoService;
import xyz.funnyboy.yygh.vo.user.LoginVo;

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
                .get(phone + "");
        if (!code.equals(mobileCode)) {
            throw new YyghException(ResultCodeEnum.CODE_ERROR);
        }

        // 手机号是否被使用
        UserInfo userInfo = baseMapper.selectOne(new LambdaQueryWrapper<UserInfo>().eq(UserInfo::getPhone, phone));
        if (userInfo == null) {
            userInfo = new UserInfo();
            userInfo.setName("");
            userInfo.setPhone(phone);
            userInfo.setStatus(1);
            baseMapper.insert(userInfo);
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
}
