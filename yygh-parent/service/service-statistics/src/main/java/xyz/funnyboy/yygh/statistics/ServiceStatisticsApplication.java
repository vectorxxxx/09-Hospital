package xyz.funnyboy.yygh.statistics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author VectorX
 * @version V1.0
 * @date 2024-01-17 20:14:02
 */
@SpringBootApplication(scanBasePackages = "xyz.funnyboy",
                       exclude = DataSourceAutoConfiguration.class)
// 开启服务注册发现
@EnableDiscoveryClient
// 开启Feign客户端
@EnableFeignClients(basePackages = {"xyz.funnyboy"})
public class ServiceStatisticsApplication
{
    public static void main(String[] args) {
        SpringApplication.run(ServiceStatisticsApplication.class, args);
    }
}
