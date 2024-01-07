package xyz.funnyboy.yygh.hosp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.funnyboy.yygh.model.hosp.HospitalSet;

/**
 * 医院套餐服务
 *
 * @author VectorX
 * @version 1.0.0
 * @date 2024/01/06
 * @see IService
 */
public interface HospitalSetService extends IService<HospitalSet>
{
    /**
     * 获取签名密钥
     *
     * @param hoscode 医院编号
     * @return {@link String}
     */
    String getSignKey(String hoscode);
}
