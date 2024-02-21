package cn.wolfcode.wolf2w.core.utils;

import cn.wolfcode.wolf2w.core.exception.BusinessException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 统一响应对象
 *
 * @param <T>
 * @author xiaoliu
 */
@Setter
@Getter
@NoArgsConstructor
public class JsonResult<T> {
    public static final int CODE_SUCCESS = 200;
    public static final String MSG_SUCCESS = "操作成功";
    public static final int CODE_NOLOGIN = 401;
    public static final int CODE_ERROR = 500;
    public static final int CODE_REGISTER_ERROR = 500100;
    public static final int CODE_SMS_ERROR = 500101;
    public static final String MSG_ERROR = "系统异常，请联系管理员";
    public static final int CODE_ERROR_PARAM = 500102;
    public static final int CODE_ERROR_DATA = 500103;

    /**
     * 区分不同结果, 而不再是true或者false
     */
    private int code;
    private String msg;
    /**
     * 除了操作结果之后, 还行携带数据返回
     */
    private T data;

    public JsonResult(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public T checkAndGet() {
        if (code != CODE_SUCCESS) {
            throw new BusinessException(code, msg);
        }
        return data;
    }

    public boolean succeed() {
        return code == CODE_SUCCESS;
    }

    public static <T> JsonResult<T> success(T data) {
        return new JsonResult<>(CODE_SUCCESS, MSG_SUCCESS, data);
    }

    public static <T> JsonResult<T> success() {
        return new JsonResult<>(CODE_SUCCESS, MSG_SUCCESS, null);
    }

    public static <T> JsonResult<T> error(int code, String msg, T data) {
        return new JsonResult<>(code, msg, data);
    }

    public static <T> JsonResult<T> error(int code, String msg) {
        return new JsonResult<>(code, msg, null);
    }

    public static <T> JsonResult<T> defaultError() {
        return new JsonResult<>(CODE_ERROR, MSG_ERROR, null);
    }

    public static <T> JsonResult<T> noLogin(String msg) {
        return new JsonResult<>(CODE_NOLOGIN, msg, null);
    }

    public static <T> JsonResult<T> noPermission() {
        return new JsonResult<>(403, "非法访问", null);
    }
}
