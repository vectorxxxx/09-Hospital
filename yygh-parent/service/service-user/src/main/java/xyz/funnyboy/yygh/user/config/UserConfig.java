package xyz.funnyboy.yygh.user.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * UserConfig
 *
 * @author VectorX
 * @version V1.0
 * @date 2024-01-13 13:54:00
 */
@Configuration
@MapperScan("xyz.funnyboy.yygh.user.mapper")
public class UserConfig
{
}
