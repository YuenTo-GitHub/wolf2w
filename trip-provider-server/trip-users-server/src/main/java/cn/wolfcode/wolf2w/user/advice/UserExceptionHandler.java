package cn.wolfcode.wolf2w.user.advice;

import cn.wolfcode.wolf2w.core.exception.BusinessException;
import cn.wolfcode.wolf2w.core.utils.JsonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

//users服务自己独有的统一异常处理逻辑
@Slf4j
@RestControllerAdvice//extends CommonExceptionHandler
public class UserExceptionHandler {
    //抓获users服务独有异常
    @ExceptionHandler(Exception.class)
    public JsonResult<?> commonExceptionHandler(Exception e) {
        log.error("拦截到异常", e);
        return JsonResult.defaultError();
    }

    @ExceptionHandler(BusinessException.class)
    public JsonResult<?> businessExceptionHandler(BusinessException e){
        log.debug("拦截到异常", e);
        if (!log.isDebugEnabled()){
            log.warn("拦截到异常，code={}，msg={}", e.getCode(), e.getMessage());
        }
        return JsonResult.error(e.getCode(), e.getMessage());
    }
}