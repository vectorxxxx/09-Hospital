package xyz.funnyboy.yygh.cmn.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * CmnConfig
 *
 * @author VectorX
 * @version V1.0
 * @date 2024-01-07 11:22:35
 */
@Configuration
@MapperScan("xyz.funnyboy.yygh.cmn.mapper")
public class CmnConfig
{
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        return new PaginationInterceptor();
    }
}
