package xyz.funnyboy.yygh.sms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author VectorX
 * @version V1.0
 * @date 2024-01-13 14:43:37
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
// 开启服务注册发现
@EnableDiscoveryClient
public class ServiceSmsApplication
{
    public static void main(String[] args) {
        SpringApplication.run(ServiceSmsApplication.class, args);
    }
}
