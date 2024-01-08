package xyz.funnyboy.yygh.cmn.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
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
     * 获取字典名称
     *
     * @param dictCode 字典code
     * @param value    字典value
     * @return {@link String}
     */
    @Override
    public String getDictName(String dictCode, String value) {
        // 如果value能唯一定位数据字典，parentDictCode可以传空，例如：省市区的value值能够唯一确定
        if (StringUtils.isEmpty(dictCode)) {
            final Dict dict = baseMapper.selectOne(new LambdaQueryWrapper<Dict>().eq(Dict::getValue, value));
            if (dict != null) {
                return dict.getName();
            }
        }

        // 父节点
        final Dict dict = this.getDictByDictCode(dictCode);
        if (dict == null) {
            return "";
        }

        // 子节点
        final Dict finalDict = baseMapper.selectOne(new LambdaQueryWrapper<Dict>()
                .eq(Dict::getParentId, dict.getId())
                .eq(Dict::getValue, value));
        if (finalDict == null) {
            return "";
        }

        return finalDict.getName();
    }

    /**
     * 按字典代码查找
     *
     * @param dictCode 字典代码
     * @return {@link List}<{@link Dict}>
     */
    @Override
    public List<Dict> findByDictCode(String dictCode) {
        final Dict dict = this.getDictByDictCode(dictCode);
        if (dict == null) {
            return null;
        }
        return this.findChildData(dict.getId());
    }

    /**
     * 通过 字典code 代码获取 dict
     *
     * @param dictCode 字典code
     * @return {@link Dict}
     */
    private Dict getDictByDictCode(String dictCode) {
        return baseMapper.selectOne(new LambdaQueryWrapper<Dict>().eq(Dict::getDictCode, dictCode));
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
