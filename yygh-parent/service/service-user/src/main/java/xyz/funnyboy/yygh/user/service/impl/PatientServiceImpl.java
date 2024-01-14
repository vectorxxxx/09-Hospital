package xyz.funnyboy.yygh.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.funnyboy.yygh.cmn.client.DictFeignClient;
import xyz.funnyboy.yygh.enums.DictEnum;
import xyz.funnyboy.yygh.model.user.Patient;
import xyz.funnyboy.yygh.user.mapper.PatientMapper;
import xyz.funnyboy.yygh.user.service.PatientService;

import java.util.List;

/**
 * @author VectorX
 * @version V1.0
 * @date 2024-01-14 21:29:21
 */
@Service
public class PatientServiceImpl extends ServiceImpl<PatientMapper, Patient> implements PatientService
{
    @Autowired
    private DictFeignClient dictFeignClient;

    /**
     * 获取就诊人列表
     *
     * @param userId 用户 ID
     * @return {@link List}<{@link Patient}>
     */
    @Override
    public List<Patient> findAllUserId(Long userId) {
        final List<Patient> patientList = baseMapper.selectList(new LambdaQueryWrapper<Patient>().eq(Patient::getUserId, userId));
        patientList.forEach(this::packPatient);
        return patientList;
    }

    /**
     * 获取患者 ID
     *
     * @param id 编号
     * @return {@link Patient}
     */
    @Override
    public Patient getPatientId(Long id) {
        return this.packPatient(baseMapper.selectById(id));
    }

    /**
     * Patient对象里面其他参数封装
     *
     * @param patient 病人
     */
    private Patient packPatient(Patient patient) {
        final String certificatesTypeString = dictFeignClient.getName(DictEnum.CERTIFICATES_TYPE.getDictCode(), patient.getCertificatesType());
        final String contractsCertificatesTypeString = dictFeignClient.getName(DictEnum.CERTIFICATES_TYPE.getDictCode(), patient.getContactsCertificatesType());

        final String provinceString = dictFeignClient.getName(patient.getProvinceCode());
        final String cityString = dictFeignClient.getName(patient.getCityCode());
        final String districtString = dictFeignClient.getName(patient.getDistrictCode());

        patient
                .getParam()
                .put("certificatesTypeString", certificatesTypeString);
        patient
                .getParam()
                .put("contractsCertificatesTypeString", contractsCertificatesTypeString);
        patient
                .getParam()
                .put("provinceString", provinceString);
        patient
                .getParam()
                .put("cityString", cityString);
        patient
                .getParam()
                .put("districtString", districtString);
        patient
                .getParam()
                .put("fullAddress", provinceString + cityString + districtString + patient.getAddress());

        return patient;
    }
}
