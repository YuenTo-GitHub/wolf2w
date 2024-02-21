package cn.wolfcode.wolf2w.comment.service;

import cn.wolfcode.wolf2w.comment.domain.StrategyComment;
import cn.wolfcode.wolf2w.comment.query.CommentQuery;
import org.springframework.data.domain.Page;

public interface IStrategyCommentService {

    /**
     * 添加
     * @param comment
     */
    void save(StrategyComment comment);

    /**
     * 分页
     * @param qo
     * @return
     */
    Page<StrategyComment> queryPage(CommentQuery qo);

    void doLike(String cid);

    void replynumIncr(Long strategyId);
}
