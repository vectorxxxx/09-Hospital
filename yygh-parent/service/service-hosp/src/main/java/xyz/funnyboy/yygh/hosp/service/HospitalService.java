package xyz.funnyboy.yygh.hosp.service;

import xyz.funnyboy.yygh.model.hosp.Hospital;

import java.util.Map;

/**
 * HospitalService
 *
 * @author VectorX
 * @version V1.0
 * @date 2024-01-07 22:24:39
 */
public interface HospitalService
{
    /**
     * 上传医院接口
     *
     * @param paramMap 参数映射
     */
    void save(Map<String, Object> paramMap);

    /**
     * 通过医院编号获取
     *
     * @param hoscode 医院编号
     * @return {@link Hospital}
     */
    Hospital getByHoscode(String hoscode);
}
