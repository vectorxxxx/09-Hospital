package xyz.funnyboy.yygh.hosp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
}
