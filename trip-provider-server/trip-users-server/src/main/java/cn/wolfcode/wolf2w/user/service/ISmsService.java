package cn.wolfcode.wolf2w.user.service;

public interface ISmsService {
    /**
     * 发送短信验证码
     * @param phone 手机号
     * @param type 使用场景，根据使用场景发送不同的短信内容
     * @return 是否发送成功
     */
    boolean sendVerifyCode(String phone, int type);
}
