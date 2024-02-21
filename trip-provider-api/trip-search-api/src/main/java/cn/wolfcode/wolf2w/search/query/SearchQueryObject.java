package cn.wolfcode.wolf2w.search.query;

import cn.wolfcode.wolf2w.core.query.QueryObject;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SearchQueryObject extends QueryObject {
    public static final int TYPE_DEST = 0;
    public static final int TYPE_STRATEGY = 1;
    public static final int TYPE_TRAVEL = 2;
    public static final int TYPE_USER = 3;
    public static final int TYPE_ALL = -1;

    private Integer type = TYPE_ALL;

}
