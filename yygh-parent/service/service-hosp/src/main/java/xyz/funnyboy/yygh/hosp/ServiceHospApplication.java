package xyz.funnyboy.yygh.hosp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * ServiceHospApplication
 *
 * @author VectorX
 * @version V1.0
 * @date 2024-01-06 15:04:51
 */
@SpringBootApplication(scanBasePackages = "xyz.funnyboy")
// 开启服务注册发现
@EnableDiscoveryClient
// 开启Feign功能
@EnableFeignClients(basePackages = "xyz.funnyboy")
public class ServiceHospApplication
{
    public static void main(String[] args) {
        SpringApplication.run(ServiceHospApplication.class, args);
    }
}
