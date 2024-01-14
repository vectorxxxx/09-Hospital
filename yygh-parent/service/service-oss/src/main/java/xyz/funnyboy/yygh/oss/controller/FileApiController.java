package xyz.funnyboy.yygh.oss.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import xyz.funnyboy.yygh.common.result.Result;
import xyz.funnyboy.yygh.oss.service.FileService;

/**
 * 对象存储服务接口
 *
 * @author VectorX
 * @version V1.0
 * @date 2024-01-14 17:45:34
 */
@Api(tags = "对象存储服务接口")
@RestController
@RequestMapping("/api/oss/file")
public class FileApiController
{
    @Autowired
    private FileService fileService;

    @ApiOperation(value = "上传文件")
    @PostMapping("/fileUpload")
    public Result fileUpload(MultipartFile file) {
        final String url = fileService.uploadFile(file);
        return Result.ok(url);
    }
}
