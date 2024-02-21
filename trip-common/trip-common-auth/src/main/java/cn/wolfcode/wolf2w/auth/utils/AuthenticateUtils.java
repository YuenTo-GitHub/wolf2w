package cn.wolfcode.wolf2w.auth.utils;

import cn.wolfcode.wolf2w.auth.service.impl.JwtServiceImpl;
import cn.wolfcode.wolf2w.core.exception.BusinessException;
import cn.wolfcode.wolf2w.core.utils.SpringContextUtil;
import cn.wolfcode.wolf2w.user.vo.LoginUserVO;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

public abstract class AuthenticateUtils {

    private static final ThreadLocal<LoginUserVO> USER_HOLDER = new ThreadLocal<>();

    public static HttpServletRequest getRequest() {
        // 在springMvc中运行，不会产生空指针异常
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    public static String getToken(){
        return getRequest().getHeader("Token");
    }

    /**
     * 获取当前登录用户
     *
     * @return 用户
     */
    public static LoginUserVO getCurrentUser() {
        LoginUserVO cacheUser = USER_HOLDER.get();
        if (cacheUser != null) {
            return cacheUser;
        }
        String token = getToken();
        JwtServiceImpl jwtService = SpringContextUtil.getBean(JwtServiceImpl.class);
        if (!StringUtils.hasText(token)){
            return null;
        }
        try {
            LoginUserVO currentUser = jwtService.getCurrentUserByToken(token);
            // 缓存用户信息
            USER_HOLDER.set(currentUser);
            return currentUser;
        } catch (BusinessException e){
            return null;
        }
    }

    public static void removeCacheUser() {
        USER_HOLDER.remove();
    }
}