package cn.wolfcode.wolf2w.user.service;

import cn.wolfcode.wolf2w.user.domain.UserInfo;
import cn.wolfcode.wolf2w.user.dto.UserInfoDTO;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 用户服务
 */
public interface IUserInfoService extends IService<UserInfo> {

    /**
     * 用户注册
     * @param phone
     * @param nickname
     * @param password
     * @param rpassword
     * @param verifyCode
     */
    void register(String phone, String nickname, String password, String rpassword, String verifyCode);

    /**
     * 用户登录
     *
     * @param username
     * @param password
     * @param request
     * @return token 用户对象
     */
    Map<String, Object> login(String username, String password, HttpServletRequest request);

    /**
     * 查询指定城市下用户集合
     * @param city
     * @return
     */
    List<UserInfo> queryByDestinationName(String city);

    // 根据手机号查找用户
    UserInfo queryByPhone(String phone);

    UserInfoDTO getDTOById(Long id);

    // 实现用户点赞、收藏
    Boolean articleInteract(Long articleId, int type, String field);
}