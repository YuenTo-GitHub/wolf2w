package cn.wolfcode.wolf2w.user.dto;


import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class UserInfoDTO implements Serializable {

    private Long id;

    private String nickname;  //昵称
    private String phone;  //手机
    private String email;  //邮箱

    private Integer gender; //性别
    private Integer level;  //用户级别
    private String city;  //所在城市
    private String headImgUrl; //头像
    private String info;  //个性签名
}