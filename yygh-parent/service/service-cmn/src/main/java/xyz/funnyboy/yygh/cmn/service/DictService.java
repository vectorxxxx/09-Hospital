package xyz.funnyboy.yygh.cmn.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;
import xyz.funnyboy.yygh.model.cmn.Dict;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * DictService
 *
 * @author VectorX
 * @version V1.0
 * @date 2024-01-07 11:04:53
 */
public interface DictService extends IService<Dict>
{
    /**
     * 查找子数据
     *
     * @param id 编号
     * @return {@link List}<{@link Dict}>
     */
    List<Dict> findChildData(Long id);

    /**
     * 导出数据
     *
     * @param response 响应
     */
    void exportData(HttpServletResponse response);

    /**
     * 导入数据
     *
     * @param file 文件
     */
    void importData(MultipartFile file);
}
