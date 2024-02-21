package cn.wolfcode.wolf2w.comment.controller;

import cn.wolfcode.wolf2w.auth.annonation.RequireLogin;
import cn.wolfcode.wolf2w.comment.domain.TravelComment;
import cn.wolfcode.wolf2w.comment.service.ITravelCommentService;
import cn.wolfcode.wolf2w.core.utils.JsonResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/travels/comments")
public class TravelCommentController {

    private final ITravelCommentService travelCommentService;

    public TravelCommentController(ITravelCommentService travelCommentService) {
        this.travelCommentService = travelCommentService;
    }

    @GetMapping("/query")
    public JsonResult<List<TravelComment>> list(Long travelId) {
        List<TravelComment> list = travelCommentService.queryByTravelId(travelId);
        return JsonResult.success(list);
    }

    @RequireLogin
    @PostMapping("/save")
    public JsonResult<?> save(TravelComment comment){
        travelCommentService.save(comment);
        // 评论数+1
        travelCommentService.replynumIncr(comment.getTravelId());
        return JsonResult.success();
    }

    /**
     * 接口中涉及获取当前登录用户采用方式：
     *
     * @GetMapping("/info") public JsonResult info(HttpServletRequest request) {
     * String token = request.getHeader("token");
     * String userStr = redisService.getCacheObject(RedisKeys.USER_LOGIN_TOKEN.join(token));
     * UserInfo userInfo = JSON.parseObject(userStr, UserInfo.class);
     * return JsonResult.success(userInfo);
     * }
     * 如果仅仅是1 2 接口到还好，一旦多了，此时存在代码重复的问题，此时怎么办？--抽--怎么抽?
     * <p>
     * 我希望： 愿景：能不能将上面获取当前登录用户对象方式转换成下面操作
     * @GetMapping("/info") public JsonResult info(UserInfo userInfo) {
     * return JsonResult.success(userInfo);
     * }
     * <p>
     * 上面操作，目前还没法实现，原因：springmvc 根本不知道怎么对userInfo类型参数进行解析
     * <p>
     * 需求：通过参数注入的方式，获取当前登录用户对象？
     * <p>
     * 完成上面需求需要使用新的知识点： springmvc参数解析器
     * <p>
     * Springmvc有2种类型参数解析器
     * <p>
     * <p>
     * 参数解析器 -- 解析接口参数用逻辑程序
     * <p>
     * 1>默认的参数解析器--自带参数解析器
     * 2>自定义参数解析器--程序员根据业务需求自定义参数解析器
     * <p>
     * 需求：自定义参数解析器，获取当前登录用户对象？
     */

    /*@GetMapping("/info")  //使用自定义参数解析器
    public JsonResult info(  @UserParam UserInfo userInfo) {
        return JsonResult.success(userInfo);
    }


    @GetMapping("/updateInfo")  //mvc默认参数解析器
    public JsonResult updateInfo(UserInfo userInfo) {
        return JsonResult.success(userInfo);
    }*/
}