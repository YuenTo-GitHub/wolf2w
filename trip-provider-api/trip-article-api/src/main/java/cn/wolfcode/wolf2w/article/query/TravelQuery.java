package cn.wolfcode.wolf2w.article.query;

import cn.wolfcode.wolf2w.article.vo.TravelRange;
import cn.wolfcode.wolf2w.core.query.QueryObject;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Setter
@Getter
public class TravelQuery extends QueryObject {

    private static final List<String> ORDER_BY_COLUMNS = Arrays.asList("viewnum", "create_time");

    private static final Map<Integer, TravelRange> TRAVEL_TIME_MAP = new HashMap<>();
    private static final Map<Integer, TravelRange> CONSUME_MAP = new HashMap<>();
    private static final Map<Integer, TravelRange> DAYS_MAP = new HashMap<>();

    static {
        TRAVEL_TIME_MAP.put(1, new TravelRange(1, 2));
        TRAVEL_TIME_MAP.put(2, new TravelRange(3, 4));
        TRAVEL_TIME_MAP.put(3, new TravelRange(5, 6));
        TRAVEL_TIME_MAP.put(4, new TravelRange(7, 8));
        TRAVEL_TIME_MAP.put(5, new TravelRange(9, 10));
        TRAVEL_TIME_MAP.put(6, new TravelRange(11, 12));
        // 人均花费
        CONSUME_MAP.put(1, new TravelRange(1, 999));
        CONSUME_MAP.put(2, new TravelRange(1000, 5999));
        CONSUME_MAP.put(3, new TravelRange(6000, 19999));
        CONSUME_MAP.put(4, new TravelRange(20000, Integer.MAX_VALUE));
        // 出行天数
        DAYS_MAP.put(1, new TravelRange(1, 3));
        DAYS_MAP.put(2, new TravelRange(4, 7));
        DAYS_MAP.put(3, new TravelRange(8, 14));
        DAYS_MAP.put(4, new TravelRange(15, 365));
    }

    private Long destId;

    private String orderBy;

    private TravelRange travelTimeRange;
    private TravelRange consumeRange;
    private TravelRange dayRange;

    public void setTravelTimeType(Integer travelTimeType) {
        this.travelTimeRange = TRAVEL_TIME_MAP.get(travelTimeType);
    }

    public void setConsumeType(Integer consumeRange) {
        this.consumeRange = CONSUME_MAP.get(consumeRange);
    }

    public void setDayType(Integer dayType) {
        this.dayRange = DAYS_MAP.get(dayType);
    }

    // 防止SQL注入
    public void setOrderBy(String orderBy) {
        if (StringUtils.hasText(orderBy) && ORDER_BY_COLUMNS.contains(orderBy)) {
            this.orderBy = orderBy;
        }
    }
}
