package cn.wolfcode.wolf2w.comment.service;

import cn.wolfcode.wolf2w.comment.domain.TravelComment;
import cn.wolfcode.wolf2w.comment.query.CommentQuery;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ITravelCommentService {

    /**
     * 添加
     * @param comment
     */
    void save(TravelComment comment);

    /**
     * 查找指定游记下的评论集合
     * @param travelId
     * @return
     */
    List<TravelComment> queryByTravelId(Long travelId);

    Page<TravelComment> queryPage(CommentQuery qo);

    void replynumIncr(Long tid);
}
