package cn.wolfcode.wolf2w.auth.service;

import cn.wolfcode.wolf2w.core.exception.BusinessException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

public interface IJwtService {
    <T> String createJwtToken(T value);

    Jws<Claims> parseJwtToken(String token) throws BusinessException;
}
