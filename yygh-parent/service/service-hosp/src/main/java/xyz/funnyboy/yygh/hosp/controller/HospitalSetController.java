package xyz.funnyboy.yygh.hosp.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import xyz.funnyboy.yygh.common.result.Result;
import xyz.funnyboy.yygh.common.utils.MD5;
import xyz.funnyboy.yygh.hosp.service.HospitalSetService;
import xyz.funnyboy.yygh.model.hosp.HospitalSet;
import xyz.funnyboy.yygh.vo.hosp.HospitalSetQueryVo;

import java.util.List;
import java.util.Random;

/**
 * HospitalSetController
 *
 * @author VectorX
 * @version V1.0
 * @date 2024-01-06 16:02:51
 */
@Api(tags = "医院设置管理")
@RestController
@RequestMapping("/admin/hosp/hospitalSet")
public class HospitalSetController
{

    @Autowired
    private HospitalSetService hospitalSetService;

    /**
     * 1 查询医院设置表所有信息
     *
     * @return {@link Result}
     */
    @ApiOperation(value = "1 获取所有医院设置")
    @GetMapping("findAll")
    public Result findAllHospitalSet() {
        //调用service的方法
        List<HospitalSet> list = hospitalSetService.list();
        return Result.ok(list);
    }

    /**
     * 2 逻辑删除医院设置
     *
     * @param id 编号
     * @return {@link Result}
     */
    @ApiOperation(value = "2 逻辑删除医院设置")
    @DeleteMapping("{id}")
    public Result removeHospSet(
            @ApiParam(name = "id",
                      value = "医院设置编号",
                      required = true)
            @PathVariable
                    Long id) {
        boolean flag = hospitalSetService.removeById(id);
        if (flag) {
            return Result.ok();
        }
        else {
            return Result.fail();
        }
    }

    /**
     * 3 条件查询带分页
     *
     * @param current            当前
     * @param limit              限制
     * @param hospitalSetQueryVo 医院设置查询 VO
     * @return {@link Result}
     */
    @ApiOperation(value = "3 条件查询带分页")
    @PostMapping("findPageHospSet/{current}/{limit}")
    public Result findPageHospSet(
            @ApiParam(name = "current",
                      value = "当前页码",
                      required = true)
            @PathVariable
                    long current,

            @ApiParam(name = "limit",
                      value = "每页记录数",
                      required = true)
            @PathVariable
                    long limit,

            @ApiParam(name = "hospitalSetQueryVo",
                      value = "查询对象",
                      required = false)
            @RequestBody(required = false)
                    HospitalSetQueryVo hospitalSetQueryVo) {
        //创建page对象，传递当前页，每页记录数
        Page<HospitalSet> page = new Page<>(current, limit);
        //构建条件
        QueryWrapper<HospitalSet> wrapper = new QueryWrapper<>();
        String hosname = hospitalSetQueryVo.getHosname();//医院名称
        String hoscode = hospitalSetQueryVo.getHoscode();//医院编号
        wrapper.like(!StringUtils.isEmpty(hosname), "hosname", hosname);
        wrapper.eq(!StringUtils.isEmpty(hoscode), "hoscode", hoscode);
        //调用方法实现分页查询
        Page<HospitalSet> pageHospitalSet = hospitalSetService.page(page, wrapper);
        //返回结果
        return Result.ok(pageHospitalSet);
    }

    /**
     * 4 添加医院设置
     *
     * @param hospitalSet 医院套装
     * @return {@link Result}
     */
    @ApiOperation(value = "4 添加医院设置")
    @PostMapping("saveHospitalSet")
    public Result saveHospitalSet(
            @ApiParam(name = "hospitalSet",
                      value = "医院设置对象",
                      required = true)
            @RequestBody
                    HospitalSet hospitalSet) {
        //设置状态 1 使用 0 不能使用
        hospitalSet.setStatus(1);
        //签名秘钥
        Random random = new Random();
        hospitalSet.setSignKey(MD5.encrypt(System.currentTimeMillis() + "" + random.nextInt(1000)));
        //调用service
        boolean save = hospitalSetService.save(hospitalSet);
        if (save) {
            return Result.ok();
        }
        else {
            return Result.fail();
        }
    }

    /**
     * 5 根据id获取医院设置
     *
     * @param id 编号
     * @return {@link Result}
     */
    @ApiOperation(value = "5 根据id获取医院设置")
    @GetMapping("getHospSet/{id}")
    public Result getHospSet(
            @ApiParam(name = "id",
                      value = "医院设置id",
                      required = true)
            @PathVariable
                    Long id) {
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        return Result.ok(hospitalSet);
    }

    /**
     * 6 修改医院设置
     *
     * @param hospitalSet 医院套装
     * @return {@link Result}
     */
    @ApiOperation(value = "6 修改医院设置")
    @PostMapping("updateHospitalSet")
    public Result updateHospitalSet(
            @ApiParam(name = "hospitalSet",
                      value = "医院设置对象",
                      required = true)
            @RequestBody
                    HospitalSet hospitalSet) {
        boolean flag = hospitalSetService.updateById(hospitalSet);
        if (flag) {
            return Result.ok();
        }
        else {
            return Result.fail();
        }
    }

    /**
     * 7 批量删除医院设置
     *
     * @param idList ID 列表
     * @return {@link Result}
     */
    @ApiOperation(value = "7 批量删除医院设置")
    @DeleteMapping("batchRemove")
    public Result batchRemoveHospitalSet(
            @ApiParam(name = "idList",
                      value = "医院设置ID列表",
                      required = true)
            @RequestBody
                    List<Long> idList) {
        hospitalSetService.removeByIds(idList);
        return Result.ok();
    }

    @ApiOperation(value = "8 医院设置锁定和解锁")
    @PutMapping("lockHospitalSet/{id}/{status}")
    public Result lockHospitalSet(
            @ApiParam(name = "id",
                      value = "医院设置ID",
                      required = true)
            @PathVariable
                    Long id,

            @ApiParam(name = "status",
                      value = "状态（0：锁定 1：正常）",
                      required = true)
            @PathVariable
                    Integer status) {
        final HospitalSet hospitalSet = hospitalSetService.getById(id);
        hospitalSet.setStatus(status);
        hospitalSetService.updateById(hospitalSet);
        return Result.ok();
    }

    @ApiOperation(value = "9 发送签名秘钥")
    @PutMapping("sendKey/{id}")
    public Result sendKey(
            @ApiParam(name = "id",
                      value = "医院设置ID",
                      required = true)
            @PathVariable
                    Long id) {
        final HospitalSet hospitalSet = hospitalSetService.getById(id);
        final String signKey = hospitalSet.getSignKey();
        final String hoscode = hospitalSet.getHoscode();
        // TODO 发送短信
        return Result.ok();
    }
}
