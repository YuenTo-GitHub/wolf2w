package cn.wolfcode.wolf2w.article.query;

import cn.wolfcode.wolf2w.core.query.QueryObject;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

@Getter
@Setter
public class StrategyQuery extends QueryObject {

    private static final List<String> ORDER_BY_COLUMNS = Arrays.asList("viewnum", "create_time");

    private Long themeId;
    private Long destId;
    private Long refid;
    private Integer type;
    private String orderBy;

    public static final int TYPE_ABROAD = 1;  //国外
    public static final int TYPE_CHINA = 2;   //国内
    public static final int TYPE_THEME = 3;     //主题

    public void setOrderBy(String orderBy) {
        if (StringUtils.hasText(orderBy) && ORDER_BY_COLUMNS.contains(orderBy)) {
            this.orderBy = orderBy;
        }
    }
}
