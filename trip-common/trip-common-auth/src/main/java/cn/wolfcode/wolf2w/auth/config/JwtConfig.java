package cn.wolfcode.wolf2w.auth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {
    // token密钥
    private String secretKey;

    // token在redis中的超时时间（毫秒）
    private long timeOut;

    // token刷新间隔时间，距离过期剩余的一段时间（如10分钟）内用户产生请求，则更新过期时间
    private long refreshTime;

    // 创建者
    private String issuer;
}
