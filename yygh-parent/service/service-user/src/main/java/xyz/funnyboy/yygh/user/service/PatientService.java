package xyz.funnyboy.yygh.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.funnyboy.yygh.model.user.Patient;

import java.util.List;

/**
 * @author VectorX
 * @version V1.0
 * @date 2024-01-14 21:27:54
 */
public interface PatientService extends IService<Patient>
{
    /**
     * 获取就诊人列表
     *
     * @param userId 用户 ID
     * @return {@link List}<{@link Patient}>
     */
    List<Patient> findAllUserId(Long userId);

    /**
     * 获取患者 ID
     *
     * @param id 编号
     * @return {@link Patient}
     */
    Patient getPatientId(Long id);
}
