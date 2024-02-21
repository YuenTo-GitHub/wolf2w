package cn.wolfcode.wolf2w.comment.redis.key;

import cn.wolfcode.wolf2w.redis.key.BaseKeyPrefix;

public class StrategiesCommentRedisKeyPrefix extends BaseKeyPrefix {
    public static final StrategiesCommentRedisKeyPrefix STRATEGIES_STAT_DATA_MAP = new StrategiesCommentRedisKeyPrefix("STRATEGIES:STAT:DATA");
    public StrategiesCommentRedisKeyPrefix(String prefix) {
        super(prefix);
    }
}
