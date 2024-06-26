package cn.wolfcode.wolf2w.auth.config;

import cn.wolfcode.wolf2w.auth.interceptor.LoginInterceptor;
import cn.wolfcode.wolf2w.auth.service.impl.JwtServiceImpl;
import cn.wolfcode.wolf2w.redis.utils.RedisCache;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
/**
 * \@Import 注解等同于以前 xml 文件中的 \<import resource="applicationContext.xml"/>
 */
@Import(WebConfig.class)
@EnableConfigurationProperties(JwtConfig.class)
public class Wolf2wJwtAutoConfiguration {

    @Bean
    public LoginInterceptor loginInterceptor(RedisCache redisCache, JwtServiceImpl jwtServiceImpl) {
        return new LoginInterceptor(redisCache, jwtServiceImpl);
    }
}
