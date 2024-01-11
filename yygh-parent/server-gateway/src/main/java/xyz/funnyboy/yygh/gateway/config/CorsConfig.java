package xyz.funnyboy.yygh.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.util.pattern.PathPatternParser;

/**
 * CorsConfig
 *
 * @author VectorX
 * @version V1.0
 * @date 2024-01-11 23:47:25
 */
@Configuration
public class CorsConfig
{
    @Bean
    public CorsWebFilter corsFilter() {
        // 创建一个CorsConfiguration对象
        final CorsConfiguration config = new CorsConfiguration();
        // 添加允许的源
        config.addAllowedOrigin("*");
        // 添加允许的方法
        config.addAllowedMethod("*");
        // 添加允许的头部
        config.addAllowedHeader("*");

        // 创建一个UrlBasedCorsConfigurationSource对象
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(new PathPatternParser());
        // 注册CorsConfiguration
        source.registerCorsConfiguration("/**", config);
        // 返回一个CorsWebFilter对象
        return new CorsWebFilter(source);
    }
}
