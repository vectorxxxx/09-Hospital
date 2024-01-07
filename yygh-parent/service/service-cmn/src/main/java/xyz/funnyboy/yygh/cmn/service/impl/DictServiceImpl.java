package xyz.funnyboy.yygh.cmn.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import xyz.funnyboy.yygh.cmn.listener.DictListener;
import xyz.funnyboy.yygh.cmn.mapper.DictMapper;
import xyz.funnyboy.yygh.cmn.service.DictService;
import xyz.funnyboy.yygh.model.cmn.Dict;
import xyz.funnyboy.yygh.vo.cmn.DictEeVo;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DictServiceImpl
 *
 * @author VectorX
 * @version V1.0
 * @date 2024-01-07 11:05:47
 */
@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService
{
    /**
     * 查找子数据
     *
     * @param id 编号
     * @return {@link List}<{@link Dict}>
     */
    @Override
    // 如果缓存存在，则直接读取缓存数据返回；
    // 如果缓存不存在，则执行方法，并把返回的结果存入缓存中
    @Cacheable(value = "dict",
               keyGenerator = "keyGenerator")
    public List<Dict> findChildData(Long id) {
        final List<Dict> dictList = baseMapper.selectList(new LambdaQueryWrapper<Dict>().eq(Dict::getParentId, id));
        dictList.forEach(dict -> dict.setHasChildren(hasChild(dict.getId())));
        return dictList;
    }

    /**
     * 导出数据
     *
     * @param response 响应
     */
    @Override
    public void exportData(HttpServletResponse response) {
        // 查询数据
        final List<Dict> dictList = baseMapper.selectList(null);
        // 组装VO
        final List<DictEeVo> dictEeVoList = dictList
                .stream()
                .map(dict -> {
                    final DictEeVo dictEeVo = new DictEeVo();
                    BeanUtils.copyProperties(dict, dictEeVo);
                    return dictEeVo;
                })
                .collect(Collectors.toList());

        // 写入数据
        final String fileName = "dict";
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        try {
            EasyExcel
                    .write(response.getOutputStream(), DictEeVo.class)
                    .sheet(fileName)
                    .doWrite(dictEeVoList);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 导入数据
     *
     * @param file 文件
     */
    @Override
    // 清空指定的缓存
    // allEntries：是否清空所有缓存，默认为 false。
    @CacheEvict(value = "dict",
                allEntries = true)
    public void importData(MultipartFile file) {
        try {
            EasyExcel
                    .read(file.getInputStream(), DictEeVo.class, new DictListener(baseMapper))
                    .sheet()
                    .doRead();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 有孩子
     *
     * @param id 编号
     * @return boolean
     */
    private boolean hasChild(Long id) {
        final Integer count = baseMapper.selectCount(new LambdaQueryWrapper<Dict>().eq(Dict::getParentId, id));
        return count > 0;
    }
}
