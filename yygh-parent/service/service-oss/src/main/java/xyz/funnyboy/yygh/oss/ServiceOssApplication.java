package xyz.funnyboy.yygh.oss;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author VectorX
 * @version V1.0
 * @date 2024-01-14 17:41:03
 */
@SpringBootApplication(scanBasePackages = "xyz.funnyboy",
                       exclude = DataSourceAutoConfiguration.class)
// 开启服务注册发现
@EnableDiscoveryClient
public class ServiceOssApplication
{
    public static void main(String[] args) {
        SpringApplication.run(ServiceOssApplication.class, args);
    }
}
