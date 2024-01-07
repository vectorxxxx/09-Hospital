package xyz.funnyboy.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.funnyboy.yygh.hosp.repository.HospitalRepository;
import xyz.funnyboy.yygh.hosp.service.HospitalService;
import xyz.funnyboy.yygh.model.hosp.Hospital;

import java.util.Date;
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
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(0);
            hospitalRepository.save(hospital);
        }
        else {
            hospitalByHoscode = new Hospital();
            // 0：未上线 1：已上线
            hospitalByHoscode.setStatus(0);
            hospitalByHoscode.setCreateTime(new Date());
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
}
