package cn.wolfcode.wolf2w.comment.controller;

import cn.wolfcode.wolf2w.auth.annonation.RequireLogin;
import cn.wolfcode.wolf2w.comment.domain.StrategyComment;
import cn.wolfcode.wolf2w.comment.query.CommentQuery;
import cn.wolfcode.wolf2w.comment.service.IStrategyCommentService;
import cn.wolfcode.wolf2w.core.utils.JsonResult;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("strategies/comments")
public class StrategyCommentController {

    private final IStrategyCommentService strategyCommentService;

    public StrategyCommentController(IStrategyCommentService strategyCommentService) {
        this.strategyCommentService = strategyCommentService;
    }

    @GetMapping("/query")
    public JsonResult<Page<StrategyComment>> queryPage(CommentQuery qo){
        Page<StrategyComment> page = strategyCommentService.queryPage(qo);
        return JsonResult.success(page);
    }

    @RequireLogin
    @PostMapping("/save")
    public JsonResult<?> save(StrategyComment comment){
        strategyCommentService.save(comment);
        // 评论数+1
        strategyCommentService.replynumIncr(comment.getStrategyId());
        return JsonResult.success();
    }

    @RequireLogin
    @PostMapping("/likes")
    public JsonResult<?> likes(String cid){
        strategyCommentService.doLike(cid);
        return JsonResult.success();
    }
}