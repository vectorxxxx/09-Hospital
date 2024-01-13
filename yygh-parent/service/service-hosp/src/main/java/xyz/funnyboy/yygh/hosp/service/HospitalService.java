package xyz.funnyboy.yygh.hosp.service;

import org.springframework.data.domain.Page;
import xyz.funnyboy.yygh.model.hosp.Hospital;
import xyz.funnyboy.yygh.vo.hosp.HospitalQueryVo;

import java.util.List;
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

    /**
     * 分页查询医院
     *
     * @param page            页数
     * @param limit           页面大小
     * @param hospitalQueryVo 医院查询VO
     * @return {@link Page}<{@link Hospital}>
     */
    Page<Hospital> selectHospPage(Integer page, Integer limit, HospitalQueryVo hospitalQueryVo);

    /**
     * 更新上线状态
     *
     * @param id     编号
     * @param status 地位
     */
    void updateStatus(String id, Integer status);

    /**
     * 获取医院详情
     *
     * @param id 编号
     * @return {@link Map}<{@link String}, {@link Object}>
     */
    Map<String, Object> getHospById(String id);

    /**
     * 获取医院名称
     *
     * @param hoscode 医院编号
     * @return {@link String}
     */
    String getHospName(String hoscode);

    /**
     * 按 医院名称 查找
     *
     * @param hosname 医院名称
     * @return {@link List}<{@link Hospital}>
     */
    List<Hospital> findByHosname(String hosname);

    /**
     * 根据医院编号获取医院挂号预约详情
     *
     * @param hoscode 医院编号
     * @return {@link Map}<{@link String}, {@link Object}>
     */
    Map<String, Object> item(String hoscode);
}
