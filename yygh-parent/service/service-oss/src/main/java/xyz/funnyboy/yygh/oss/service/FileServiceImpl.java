package xyz.funnyboy.yygh.oss.service;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import xyz.funnyboy.yygh.oss.utils.ConstantOssPropertiesUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * @author VectorX
 * @version V1.0
 * @date 2024-01-14 17:45:03
 */
@Service
@Slf4j
public class FileServiceImpl implements FileService
{
    /**
     * 上传文件
     *
     * @param file 文件
     * @return {@link String}
     */
    @Override
    public String uploadFile(MultipartFile file) {
        // Endpoint以杭州为例，其它Region请按实际情况填写。
        String endpoint = ConstantOssPropertiesUtils.EDNPOINT;
        String accessKeyId = ConstantOssPropertiesUtils.ACCESS_KEY_ID;
        String accessKeySecret = ConstantOssPropertiesUtils.SECRECT;
        String bucketName = ConstantOssPropertiesUtils.BUCKET;

        try {
            // oss 实例
            final OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

            // 文件名称
            final String originalFilename = file.getOriginalFilename();
            final InputStream content = file.getInputStream();
            final String newFileName = new DateTime().toString("yyyy/MM/dd") + "/" + UUID
                    .randomUUID()
                    .toString()
                    .replaceAll("-", "") + originalFilename;

            // 上传文件
            ossClient.putObject(bucketName, newFileName, content);
            ossClient.shutdown();
            log.info("上传文件成功：" + newFileName);
            return "https://" + bucketName + "." + endpoint + "/" + newFileName;
        }
        catch (IOException e) {
            log.error("上传文件失败：" + e.getMessage(), e);
            return null;
        }
    }
}
