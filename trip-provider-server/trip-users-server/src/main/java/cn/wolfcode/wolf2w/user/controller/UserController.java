package cn.wolfcode.wolf2w.user.controller;

import cn.wolfcode.wolf2w.auth.annonation.RequireLogin;
import cn.wolfcode.wolf2w.core.exception.BusinessException;
import cn.wolfcode.wolf2w.core.query.QueryObject;
import cn.wolfcode.wolf2w.core.utils.JsonResult;
import cn.wolfcode.wolf2w.user.domain.UserInfo;
import cn.wolfcode.wolf2w.user.dto.UserInfoDTO;
import cn.wolfcode.wolf2w.user.service.ISmsService;
import cn.wolfcode.wolf2w.user.service.IUserInfoService;
import cn.wolfcode.wolf2w.user.utils.ValidateDataUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequireLogin
@RestController
@RequestMapping("/users")
public class UserController {

    private final ISmsService smsService;

    private final IUserInfoService userInfoService;

    public UserController(IUserInfoService userInfoService, ISmsService smsService) {
        this.userInfoService = userInfoService;
        this.smsService = smsService;
    }

    @GetMapping("/get")
    public UserInfo get(@Positive Long id) {
        return userInfoService.getById(id);
    }

    @GetMapping("/list")
    public JsonResult<List<UserInfo>> list() {
        return JsonResult.success(userInfoService.list());
    }

    @GetMapping("/getUserInfoDTO")
    public JsonResult<UserInfoDTO> getUserInfoDTO(Long id) {
        return JsonResult.success(userInfoService.getDTOById(id));
    }

    // 检查数据库中是否存在此手机号
    @GetMapping("/checkPhone")
    public JsonResult<Boolean> checkPhone(@NotBlank String phone) {
        // 判断手机号码格式是否正确
        if (!ValidateDataUtils.isValidPhoneNumber(phone)) {
            throw new BusinessException(JsonResult.CODE_ERROR_PARAM, "手机号不正确！");
        }
        return JsonResult.success(userInfoService.queryByPhone(phone) != null);
    }

    /**
     * 发送短信验证码
     *
     * @param phone 手机号
     * @param type  用途，0登录、1注册
     */
    @GetMapping("/sendVerifyCode")
    public JsonResult<?> sendVerifyCode(@NotBlank String phone, @PositiveOrZero int type) {
        Boolean registered = checkPhone(phone).getData();
        if (!registered && type == 1 && smsService.sendVerifyCode(phone, type)) {
            // 用户正在注册
            return JsonResult.success();
        } else if (registered && type == 0 && smsService.sendVerifyCode(phone, type)) {
            // 用户正在登录
            return JsonResult.success();
        }
        // 发送验证码失败
        return JsonResult.defaultError();
    }

    @PostMapping("/register")
    public JsonResult<?> register(@NotBlank String phone, @NotBlank String nickname, @NotBlank String password, @NotBlank String rpassword, @NotBlank String verifyCode) {
        // 判断手机号码格式是否正确
        if (!ValidateDataUtils.isValidPhoneNumber(phone)) {
            throw new BusinessException(JsonResult.CODE_REGISTER_ERROR, "手机号不正确！");
        }
        //判断两次输入密码是否一致
        Assert.isTrue(password.equals(rpassword), "两次输入密码不一致");
        userInfoService.register(phone, nickname, password, rpassword, verifyCode);
        return JsonResult.success();  //正常
    }

    @PostMapping("/login")
    public JsonResult<Map<String, Object>> login(@NotBlank String phone, @NotBlank String password, HttpServletRequest request) {
        // 判断手机号码格式是否正确
        if (!ValidateDataUtils.isValidPhoneNumber(phone)) {
            return JsonResult.error(JsonResult.CODE_REGISTER_ERROR, "手机号不正确！");
        }
        Map<String, Object> data = userInfoService.login(phone, password, request);
        String errorMsg;
        if ((errorMsg = (String) data.get("errorMsg")) != null) {
            return JsonResult.error(JsonResult.CODE_ERROR_DATA, errorMsg);
        }
        return JsonResult.success(data);
    }

    /*@PostMapping("/logout")
    public JsonResult<?> logout(){
        // TODO: 退出登录时将redis中的数据保存到mongodb
    }*/

    // type=-1 -> 游记，type=1 -> 攻略
    @PostMapping({"/likes", "/favor"})
    public JsonResult<Boolean> articleInteract(Long articleId, int type, HttpServletRequest request) {
        String field = request.getRequestURI().substring(7);
        Boolean interacted = userInfoService.articleInteract(articleId, type, field);
        return JsonResult.success(interacted);
    }

    @PostMapping("/search")
    public JsonResult<List<UserInfoDTO>> searchList(@RequestBody QueryObject qo) {
        QueryWrapper<UserInfo> wrapper = new QueryWrapper<UserInfo>().last("limit "+ qo.getOffset() + "," + qo.getSize());
        List<UserInfo> userInfos = userInfoService.list(wrapper);
        List<UserInfoDTO> userInfoDTOs = userInfos.stream().map(UserInfo::toDto).collect(Collectors.toList());
        return JsonResult.success(userInfoDTOs);
    }

    @GetMapping("/findByDestName")
    public JsonResult<List<UserInfo>> findByDestName(@RequestParam String destName){
        return JsonResult.success(userInfoService.queryByDestinationName(destName));
    }
}
