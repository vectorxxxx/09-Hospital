package xyz.funnyboy.yygh.hosp.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * HospConfig
 *
 * @author VectorX
 * @version V1.0
 * @date 2024-01-07 11:25:08
 */
@Configuration
@MapperScan("xyz.funnyboy.yygh.hosp.mapper")
public class HospConfig
{
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        return new PaginationInterceptor();
    }
}
