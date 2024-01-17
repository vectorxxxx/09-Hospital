package xyz.funnyboy.yygh.task;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author VectorX
 * @version V1.0
 * @date 2024-01-17 19:10:16
 */
@SpringBootApplication(scanBasePackages = "xyz.funnyboy",
                       exclude = DataSourceAutoConfiguration.class)
// 开启服务注册发现
@EnableDiscoveryClient
public class ServiceTaskApplication
{
    public static void main(String[] args) {
        SpringApplication.run(ServiceTaskApplication.class, args);
    }
}
