package xyz.funnyboy.yygh.oss.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author VectorX
 * @version V1.0
 * @date 2024-01-14 17:44:23
 */
public interface FileService
{
    /**
     * 上传文件
     *
     * @param file 文件
     * @return {@link String}
     */
    String uploadFile(MultipartFile file);
}
