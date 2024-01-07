package xyz.funnyboy.yygh.hosp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.funnyboy.yygh.common.exception.YyghException;
import xyz.funnyboy.yygh.common.result.ResultCodeEnum;
import xyz.funnyboy.yygh.hosp.mapper.HospitalSetMapper;
import xyz.funnyboy.yygh.hosp.service.HospitalSetService;
import xyz.funnyboy.yygh.model.hosp.HospitalSet;

/**
 * HospitalSetServiceImpl
 *
 * @author VectorX
 * @version V1.0
 * @date 2024-01-06 15:24:53
 */
@Service
public class HospitalSetServiceImpl extends ServiceImpl<HospitalSetMapper, HospitalSet> implements HospitalSetService
{
    @Autowired
    private HospitalSetMapper hospitalSetMapper;

    /**
     * 获取签名密钥
     *
     * @param hoscode 医院编号
     * @return {@link String}
     */
    @Override
    public String getSignKey(String hoscode) {
        final HospitalSet hospitalSet = hospitalSetMapper.selectOne(new LambdaQueryWrapper<HospitalSet>().eq(HospitalSet::getHoscode, hoscode));
        if (hospitalSet == null) {
            throw new YyghException(ResultCodeEnum.HOSPITAL_OPEN);
        }
        if (hospitalSet.getStatus() == 0) {
            throw new YyghException(ResultCodeEnum.HOSPITAL_LOCK);
        }
        return hospitalSet.getSignKey();
    }
}
