package cn.wolfcode.wolf2w.user.vo;

import lombok.Data;

@Data
public class LoginUserVO {
    private Long id;
    private String nickname;  //昵称
    private String phone;  //手机
    private String email;  //邮箱
    private Integer gender; //性别
    private Integer level = 0;  //用户级别
    private String city;  //所在城市
    private String headImgUrl; //头像
    private String info;  //个性签名

    private Long loginTime; // 登录时间
    private Long expireTime; // 登录到期时间（毫秒）
}
