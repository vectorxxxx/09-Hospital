package xyz.funnyboy.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import xyz.funnyboy.yygh.cmn.client.DictFeignClient;
import xyz.funnyboy.yygh.hosp.repository.HospitalRepository;
import xyz.funnyboy.yygh.hosp.service.HospitalService;
import xyz.funnyboy.yygh.model.hosp.Hospital;
import xyz.funnyboy.yygh.vo.hosp.HospitalQueryVo;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * HospitalServiceImpl
 *
 * @author VectorX
 * @version V1.0
 * @date 2024-01-07 22:24:51
 */
@Service
public class HospitalServiceImpl implements HospitalService
{
    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private DictFeignClient dictFeignClient;

    /**
     * 上传医院接口
     *
     * @param paramMap 参数映射
     */
    @Override
    public void save(Map<String, Object> paramMap) {
        // map 转为强类型，方便取值
        final String mapStr = JSONObject.toJSONString(paramMap);
        final Hospital hospital = JSONObject.parseObject(mapStr, Hospital.class);

        // 根据医院编号查询医院是否存在
        Hospital hospitalByHoscode = hospitalRepository.getHospitalByHoscode(hospital.getHoscode());

        // 存在则更新，不存在则插入
        if (hospitalByHoscode != null) {
            hospitalByHoscode.setStatus(hospital.getStatus());
            hospitalByHoscode.setCreateTime(hospital.getCreateTime());
            hospitalByHoscode.setUpdateTime(new Date());
            hospitalByHoscode.setIsDeleted(0);
            hospitalRepository.save(hospitalByHoscode);
        }
        else {
            // 0：未上线 1：已上线
            hospital.setStatus(0);
            hospital.setCreateTime(new Date());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(0);
            hospitalRepository.insert(hospital);
        }

    }

    /**
     * 通过医院编号获取
     *
     * @param hoscode 医院编号
     * @return {@link Hospital}
     */
    @Override
    public Hospital getByHoscode(String hoscode) {
        return hospitalRepository.getHospitalByHoscode(hoscode);
    }

    /**
     * 分页查询医院
     *
     * @param page            页数
     * @param limit           页面大小
     * @param hospitalQueryVo 医院查询VO
     * @return {@link Page}<{@link Hospital}>
     */
    @Override
    public Page<Hospital> selectHospPage(Integer page, Integer limit, HospitalQueryVo hospitalQueryVo) {
        // 分页条件
        final PageRequest pageRequest = PageRequest.of(page - 1, limit);

        // 查询条件
        final Hospital hospital = new Hospital();
        BeanUtils.copyProperties(hospitalQueryVo, hospital);

        // 匹配器
        final ExampleMatcher exampleMatcher = ExampleMatcher
                .matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true);
        final Example<Hospital> example = Example.of(hospital, exampleMatcher);

        // 分页查询
        final Page<Hospital> pages = hospitalRepository.findAll(example, pageRequest);

        // 医院等级封装
        pages
                .getContent()
                .forEach(this::setHospitalHosType);
        return pages;
    }

    /**
     * 更新上线状态
     *
     * @param id     编号
     * @param status 地位
     */
    @Override
    public void updateStatus(String id, Integer status) {
        if (status == 0 || status == 1) {
            final Hospital hospital = hospitalRepository
                    .findById(id)
                    .get();
            hospital.setStatus(status);
            hospital.setUpdateTime(new Date());
            hospitalRepository.save(hospital);
        }
    }

    /**
     * 获取医院详情
     *
     * @param id 编号
     * @return {@link Map}<{@link String}, {@link Object}>
     */
    @Override
    public Map<String, Object> getHospById(String id) {
        Map<String, Object> result = new HashMap<>();
        // 医院详情
        Hospital hospital = hospitalRepository
                .findById(id)
                .orElse(null);
        if (hospital == null) {
            return result;
        }

        // 设置医院等级名称和地址等信息
        hospital = setHospitalHosType(hospital);

        // 组装数据
        result.put("hospital", hospital);
        result.put("bookingRule", hospital.getBookingRule());
        // 没必要重复获取数据
        hospital.setBookingRule(null);
        return result;
    }

    /**
     * 获取医院名称
     *
     * @param hoscode 医院编号
     * @return {@link String}
     */
    @Override
    public String getHospName(String hoscode) {
        final Hospital hospital = hospitalRepository.getHospitalByHoscode(hoscode);
        if (hospital != null) {
            return hospital.getHosname();
        }
        return "";
    }

    /**
     * 设置医院等级
     *
     * @param hospital 医院
     * @return {@link Hospital}
     */
    private Hospital setHospitalHosType(Hospital hospital) {
        // 获取医院等级名称
        final String hostype = dictFeignClient.getName("Hostype", hospital.getHostype());
        // 查询省、市、地区
        final String province = dictFeignClient.getName(hospital.getProvinceCode());
        final String city = dictFeignClient.getName(hospital.getCityCode());
        final String district = dictFeignClient.getName(hospital.getDistrictCode());

        hospital
                .getParam()
                .put("fullAddress", province + city + district);
        hospital
                .getParam()
                .put("hostypeString", hostype);
        return hospital;
    }
}
