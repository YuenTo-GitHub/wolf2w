package cn.wolfcode.wolf2w.core.exception;

import cn.wolfcode.wolf2w.core.utils.JsonResult;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private Integer code = JsonResult.CODE_ERROR;

    public BusinessException() {
        super(JsonResult.MSG_ERROR);
    }

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }
}
