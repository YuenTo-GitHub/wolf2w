package cn.wolfcode.wolf2w.comment.query;

import cn.wolfcode.wolf2w.core.query.QueryObject;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentQuery extends QueryObject {
    private Long articleId;
}