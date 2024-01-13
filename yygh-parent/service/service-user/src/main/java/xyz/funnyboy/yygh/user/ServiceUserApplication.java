package xyz.funnyboy.yygh.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * ServiceUserApplication
 *
 * @author VectorX
 * @version V1.0
 * @date 2024-01-13 13:51:18
 */
@SpringBootApplication(scanBasePackages = "xyz.funnyboy")
// 开启服务注册发现
@EnableDiscoveryClient
// 开启feign功能
@EnableFeignClients(basePackages = "xyz.funnyboy")
public class ServiceUserApplication
{
    public static void main(String[] args) {
        SpringApplication.run(ServiceUserApplication.class, args);
    }
}
