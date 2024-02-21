package cn.wolfcode.wolf2w.user.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class AliyunSMSConfig {

    @Value("${aliyun.access-key.id}")
    private String accessKeyId;

    @Value("${aliyun.access-key.secret}")
    private String accessKeySecret;

    /*---------------------------- sms短信服务配置 ----------------------------*/
    @Value("${aliyun.dysms.endpoint}")
    private String endpoint;

    @Value("${aliyun.dysms.template-code}")
    private String templateCode;

    @Value("${aliyun.dysms.sign-name}")
    private String signName;

    // 验证码有效期（单位：分钟）
    @Value("${verify-code.time-out}")
    private Long timeOut;

    // 验证码长度
    @Value("${verify-code.length}")
    private int length;
}