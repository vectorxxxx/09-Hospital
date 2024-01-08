package xyz.funnyboy.yygh.cmn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * ServiceCmnApplication
 *
 * @author VectorX
 * @version V1.0
 * @date 2024-01-07 10:56:55
 */
@SpringBootApplication(scanBasePackages = "xyz.funnyboy")
// 开启服务注册发现
@EnableDiscoveryClient
public class ServiceCmnApplication
{
    public static void main(String[] args) {
        SpringApplication.run(ServiceCmnApplication.class, args);
    }
}
