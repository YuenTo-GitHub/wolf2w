package cn.wolfcode.wolf2w.comment.redis.key;

import cn.wolfcode.wolf2w.redis.key.BaseKeyPrefix;

public class TravelsCommentRedisKeyPrefix extends BaseKeyPrefix {
    public static final TravelsCommentRedisKeyPrefix TRAVELS_STAT_DATA_MAP = new TravelsCommentRedisKeyPrefix("TRAVELS:STAT:DATA");
    public TravelsCommentRedisKeyPrefix(String prefix) {
        super(prefix);
    }
}
