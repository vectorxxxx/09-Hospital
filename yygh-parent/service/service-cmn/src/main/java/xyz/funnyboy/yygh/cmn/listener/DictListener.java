package xyz.funnyboy.yygh.cmn.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import org.springframework.beans.BeanUtils;
import xyz.funnyboy.yygh.cmn.mapper.DictMapper;
import xyz.funnyboy.yygh.model.cmn.Dict;
import xyz.funnyboy.yygh.vo.cmn.DictEeVo;

/**
 * @author VectorX
 * @version V1.0
 * @date 2024-01-07 12:52:41
 */
public class DictListener extends AnalysisEventListener<DictEeVo>
{
    private DictMapper dictMapper;

    public DictListener(DictMapper dictMapper) {
        this.dictMapper = dictMapper;
    }

    @Override
    public void invoke(DictEeVo dictEeVo, AnalysisContext analysisContext) {
        // 先删除
        dictMapper.deleteById(dictEeVo.getId());

        // 再新增
        Dict dict = new Dict();
        BeanUtils.copyProperties(dictEeVo, dict);
        dictMapper.insert(dict);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }
}
