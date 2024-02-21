package cn.wolfcode.wolf2w.user.service.impl;

import cn.wolfcode.wolf2w.user.config.AliyunSMSConfig;
import cn.wolfcode.wolf2w.user.redis.key.UserRedisKeyPrefix;
import cn.wolfcode.wolf2w.user.service.ISmsService;
import cn.wolfcode.wolf2w.user.utils.ValidateCodeUtils;
import cn.wolfcode.wolf2w.user.utils.ValidateDataUtils;
import cn.wolfcode.wolf2w.redis.utils.RedisCache;
import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.dysmsapi20170525.models.SendSmsResponseBody;
import com.aliyun.tea.TeaException;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teautil.models.RuntimeOptions;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class SmsServiceImpl implements ISmsService {

    private final RedisCache redisCache;

    private final AliyunSMSConfig aliyunSMSConfig;

    public SmsServiceImpl(RedisCache redisCache, AliyunSMSConfig aliyunSMSConfig) {
        this.redisCache = redisCache;
        this.aliyunSMSConfig = aliyunSMSConfig;
    }

    /**
     * 发送短信验证码
     *
     * @param phone 手机号
     * @param type  使用场景（登录/注册/忘记密码等），根据使用场景发送不同的短信内容
     * @return 是否发送成功
     */
    @Override
    public boolean sendVerifyCode(String phone, int type) {
        // 验证手机号合法性
        if (!ValidateDataUtils.isValidPhoneNumber(phone)){
            return false;
        }
        // 创建验证码
        String code = ValidateCodeUtils.generateValidateCode(aliyunSMSConfig.getLength()).toString();
        log.info("验证码是：{}", code);
        // 发送验证码，可以集成sentinel限流
        SendSmsRequest sendSmsRequest = new SendSmsRequest()
                .setSignName(aliyunSMSConfig.getSignName())
                .setTemplateCode(aliyunSMSConfig.getTemplateCode())
                .setPhoneNumbers(phone)
                .setTemplateParam("{\"code\":\"" + code + "\"}");
        RuntimeOptions runtime = new RuntimeOptions();
        try {
            Config config = new Config()
                    // 必填，您的 AccessKey ID
                    .setAccessKeyId(aliyunSMSConfig.getAccessKeyId())
                    // 必填，您的 AccessKey Secret
                    .setAccessKeySecret(aliyunSMSConfig.getAccessKeySecret());
            // Endpoint 请参考 https://api.aliyun.com/product/Dysmsapi
            config.endpoint = aliyunSMSConfig.getEndpoint();
            Client smsClient = new Client(config);
            // 复制代码运行请自行打印 API 的返回值
            SendSmsResponse smsResponse = smsClient.sendSmsWithOptions(sendSmsRequest, runtime);
            SendSmsResponseBody body = smsResponse.getBody();
            if ("OK".equals(body.getCode())){
                log.info("发送成功");
                // 发送成功，设置Key
                UserRedisKeyPrefix keyPrefix = switch (type) {
                    case 0 -> UserRedisKeyPrefix.USER_LOGIN_VERIFY_CODE_STRING;
                    case 1 -> UserRedisKeyPrefix.USER_REGISTER_VERIFY_CODE_STRING;
                    default -> throw new IllegalStateException("Unexpected value: " + type);
                };
                // 保存验证码，10分钟有效
                redisCache.setCacheObject(keyPrefix.getKey(phone), code, aliyunSMSConfig.getTimeOut(), TimeUnit.MINUTES);
                return true;
            }
            log.info("发送成功");
            ObjectMapper mapper = new ObjectMapper();
            String respJson = mapper.writeValueAsString(body);
            log.error("发送失败，响应结果为：{}", respJson);
        } catch (TeaException error) {
            // 如有需要，请打印 error
            com.aliyun.teautil.Common.assertAsString(error.message);
        } catch (Exception _error) {
            TeaException error = new TeaException(_error.getMessage(), _error);
            // 如有需要，请打印 error
            com.aliyun.teautil.Common.assertAsString(error.message);
        }
        return false;
    }
}