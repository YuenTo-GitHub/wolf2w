package cn.wolfcode.wolf2w.auth.service.impl;

import cn.wolfcode.wolf2w.auth.config.JwtConfig;
import cn.wolfcode.wolf2w.auth.service.IJwtService;
import cn.wolfcode.wolf2w.core.exception.BusinessException;
import cn.wolfcode.wolf2w.core.utils.SpringContextUtil;
import cn.wolfcode.wolf2w.user.redis.key.UserRedisKeyPrefix;
import cn.wolfcode.wolf2w.user.vo.LoginUserVO;
import cn.wolfcode.wolf2w.redis.utils.RedisCache;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.DefaultClaims;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Slf4j
@Getter
@Service
public class JwtServiceImpl implements IJwtService {

    private final JwtConfig jwtConfig;

    public JwtServiceImpl(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    /**
     * 根据用户信息，使用jwt创建token
     *
     * @param uuid 用户uuid
     * @return token
     */
    @Override
    public <T> String createJwtToken(T uuid) {
        Claims claims = new DefaultClaims();
        claims.put("uuid", uuid);
        long now = System.currentTimeMillis();
        // token生成时间
        Date createdDate = new Date(now);
        claims.setIssuedAt(createdDate);
        claims.setIssuer(jwtConfig.getIssuer());
        // 使用jwt创建token
        byte[] keyBytes = Decoders.BASE64.decode(jwtConfig.getSecretKey());
        Key key = Keys.hmacShaKeyFor(keyBytes);
        return Jwts.builder().setClaims(claims).signWith(key).compact();
    }

    /**
     * 解析token
     *
     * @param token token字符串
     * @return Jws对象
     */
    @Override
    public Jws<Claims> parseJwtToken(String token) throws BusinessException {
        try {
            return Jwts.parserBuilder().setSigningKey(jwtConfig.getSecretKey()).build().parseClaimsJws(token);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "token为空！");
        } catch (Exception e) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED.value(), "用户未认证！");
        }
    }

    public LoginUserVO getCurrentUserByToken(String token) throws BusinessException {
        Jws<Claims> jws = parseJwtToken(token);
        Claims claims = jws.getBody();
        String uuid = claims.get("uuid", String.class);
        String userLoginKey = UserRedisKeyPrefix.USER_LOGIN_INFO_STRING.getKey(uuid);
        RedisCache redisCache = SpringContextUtil.getBean(RedisCache.class);
        return redisCache.getCacheObject(userLoginKey);
    }
}
