package cn.wolfcode.wolf2w.user.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class ValidateDataUtils {

    private ValidateDataUtils() {}

    /**
     * 验证手机号是否合法
     * @param phone 手机号
     * @return 合法性
     */
    public static boolean isValidPhoneNumber(String phone){
        // 定义手机号的正则表达式，支持常见的手机号格式
        String regex = "^1(3\\d|4[5-9]|5[0-35-9]|6[567]|7[0-8]|8\\d|9[0-35-9])\\d{8}$";
        // 编译正则表达式
        Pattern pattern = Pattern.compile(regex);
        // 使用正则表达式匹配手机号
        Matcher matcher = pattern.matcher(phone);
        // 返回匹配结果
        return matcher.matches();
    }
}
