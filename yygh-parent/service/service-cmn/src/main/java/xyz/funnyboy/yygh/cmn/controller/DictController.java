package xyz.funnyboy.yygh.cmn.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import xyz.funnyboy.yygh.cmn.service.DictService;
import xyz.funnyboy.yygh.common.result.Result;
import xyz.funnyboy.yygh.model.cmn.Dict;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * DictController
 *
 * @author VectorX
 * @version V1.0
 * @date 2024-01-07 11:09:20
 */
@Api(tags = "数据字典接口")
@RestController
@RequestMapping("/admin/cmn/dict")
@CrossOrigin
public class DictController
{
    @Autowired
    private DictService dictService;

    @ApiOperation(value = "根据数据id获取子数据列表")
    @GetMapping("findChildData/{id}")
    public Result findChildData(
            @ApiParam(value = "数据id",
                      name = "id",
                      required = true)
            @PathVariable
                    Long id) {
        final List<Dict> childData = dictService.findChildData(id);
        return Result.ok(childData);
    }

    @ApiOperation(value = "导出数据")
    @GetMapping("exportData")
    public void exportData(HttpServletResponse response) {
        dictService.exportData(response);
    }

    @ApiOperation(value = "导入数据")
    @PostMapping("importData")
    public Result importData(MultipartFile file) {
        dictService.importData(file);
        return Result.ok();
    }
}
