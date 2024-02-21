package cn.wolfcode.wolf2w.auth.interceptor;

import cn.wolfcode.wolf2w.auth.annonation.RequireLogin;
import cn.wolfcode.wolf2w.auth.config.JwtConfig;
import cn.wolfcode.wolf2w.auth.service.impl.JwtServiceImpl;
import cn.wolfcode.wolf2w.auth.utils.AuthenticateUtils;
import cn.wolfcode.wolf2w.core.exception.BusinessException;
import cn.wolfcode.wolf2w.core.utils.JsonResult;
import cn.wolfcode.wolf2w.user.redis.key.UserRedisKeyPrefix;
import cn.wolfcode.wolf2w.user.vo.LoginUserVO;
import cn.wolfcode.wolf2w.redis.utils.RedisCache;
import com.alibaba.fastjson2.JSONObject;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class LoginInterceptor implements HandlerInterceptor {
    private final RedisCache redisCache;

    private final JwtServiceImpl jwtServiceImpl;

    private final JwtConfig jwtConfig;

    public LoginInterceptor(RedisCache redisCache, JwtServiceImpl jwtServiceImpl) {
        this.redisCache = redisCache;
        this.jwtServiceImpl = jwtServiceImpl;
        this.jwtConfig = jwtServiceImpl.getJwtConfig();
    }

    /*
    需求：若用户在登录即将过期（还剩10分钟）的时候使用系统，则刷新过期时间
    技术方案：
        将用户对象存入redis，redis中的key作为jwtToken返回给前端
        jwtToken本身没有过期时间，redis有过期时间，更新的是redis的过期时间
        缺点是每次请求都要访问redis
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.debug("拦截到请求：{}", request.getRequestURI());
        if (!(handler instanceof HandlerMethod)){
            // 静态资源的请求或CORS预请求，不需要拦截
            return true;
        }
        // 从HandlerMethod对象中获取对应的控制器对象
        HandlerMethod hm = (HandlerMethod) handler;
        Class<?> controller = hm.getBeanType();
        RequireLogin classAnno = controller.getAnnotation(RequireLogin.class);
        RequireLogin methodAnno = hm.getMethodAnnotation(RequireLogin.class);
        if (classAnno == null && methodAnno == null){
            // 此控制器无RequireLogin注解，不需要拦截
            return true;
        } else if ("feign".equals(request.getHeader("Requester"))){
            // feign请求，放行
            return true;
        }
        // 从请求头中获取token
        String token = request.getHeader("Token");
        log.debug(token);
        // 解析token
        try{
            Jws<Claims> jws = jwtServiceImpl.parseJwtToken(token);
            Claims claims = jws.getBody();
            String uuid = claims.get("uuid", String.class);
            String userLoginKey = UserRedisKeyPrefix.USER_LOGIN_INFO_STRING.getKey(uuid);
            long expireTime;
            LoginUserVO loginUser = redisCache.getCacheObject(userLoginKey);
            if (loginUser == null){
                log.info("登录过期！");
                response.setContentType("application/json");
                String jsonString = JSONObject.toJSONString(JsonResult.noLogin("登录过期，请重新登录"));
                response.getWriter().write(jsonString);
                return false;
            } else if ((expireTime = loginUser.getExpireTime()) - (System.currentTimeMillis()) <= jwtConfig.getRefreshTime()){
                // 本次操作离登录过期时间还有十分钟，则刷新过期时间
                loginUser.setExpireTime(expireTime + jwtConfig.getTimeOut());
                redisCache.setCacheObject(userLoginKey, loginUser, jwtConfig.getTimeOut(), TimeUnit.MILLISECONDS);
            }
            // 登录有效
            return true;
        } catch (BusinessException e){
            response.setContentType("application/json");
            String jsonString = JSONObject.toJSONString(JsonResult.noLogin("请先登录！"));
            response.getWriter().write(jsonString);
            return false;
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // 线程即将完成本次请求，将线程空间内存储的数据清空，防止内存泄漏
        AuthenticateUtils.removeCacheUser();
    }
}