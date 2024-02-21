package cn.wolfcode.wolf2w.article.redis.key;

import cn.wolfcode.wolf2w.redis.key.BaseKeyPrefix;

public class ArticleRedisKeyPrefix extends BaseKeyPrefix {

    public static final ArticleRedisKeyPrefix STRATEGIES_STAT_DATA_MAP = new ArticleRedisKeyPrefix("STRATEGIES:STAT:DATA");
    public static final ArticleRedisKeyPrefix TRAVELS_STAT_DATA_MAP = new ArticleRedisKeyPrefix("TRAVELS:STAT:DATA");
    public static final ArticleRedisKeyPrefix STRATEGIES_STAT_COUNT_RANK_ZSET = new ArticleRedisKeyPrefix("STRATEGIES:STAT:COUNT:RANK");
    public static final ArticleRedisKeyPrefix TRAVELS_STAT_COUNT_RANK_ZSET = new ArticleRedisKeyPrefix("TRAVELS:STAT:COUNT:RANK");
    public ArticleRedisKeyPrefix(String prefix) {
        super(prefix);
    }
}
