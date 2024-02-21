package cn.wolfcode.wolf2w.data.job;

import cn.wolfcode.wolf2w.article.domain.Strategy;
import cn.wolfcode.wolf2w.article.domain.Travel;
import cn.wolfcode.wolf2w.article.redis.key.ArticleRedisKeyPrefix;
import cn.wolfcode.wolf2w.data.mapper.StrategyMapper;
import cn.wolfcode.wolf2w.data.mapper.TravelMapper;
import cn.wolfcode.wolf2w.redis.utils.RedisCache;
import cn.wolfcode.wolf2w.user.domain.UserStrategyFavorite;
import cn.wolfcode.wolf2w.user.domain.UserTravelFavorite;
import cn.wolfcode.wolf2w.user.redis.key.UserRedisKeyPrefix;
import cn.wolfcode.wolf2w.user.vo.UserArticleInteractionVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * 1、定期从redis写入viewmum、likesnum和favornum到文章的mysql表
 * 2、12点将ArticleInteractionVO对象中的likesList清空，使用户第二天能继续顶文章，
 * 将viewList、favorList写入mongodb
 */
@Slf4j
@Component
public class DataStatisticJob {

    private final StrategyMapper strategyMapper;

    private final TravelMapper travelMapper;

    private final MongoTemplate template;

    private final RedisCache redisCache;

    public DataStatisticJob(StrategyMapper strategyMapper, TravelMapper travelMapper, MongoTemplate template, RedisCache redisCache) {
        this.strategyMapper = strategyMapper;
        this.travelMapper = travelMapper;
        this.template = template;
        this.redisCache = redisCache;
    }

    // 每5分钟统计文章数据到MYSQL
    @Scheduled(cron = "0 */5 * * * *")
    public void statisticNum() {
        // 根据key变动次数，同步redis中的一部分数据到mysql
        Set<Integer> articleIds = redisCache.zsetRangeByScore(ArticleRedisKeyPrefix.STRATEGIES_STAT_COUNT_RANK_ZSET, 4, Integer.MAX_VALUE);
        if (doStatisticNum(articleIds, 1))
            redisCache.zsetRemoveRangeByScore(ArticleRedisKeyPrefix.STRATEGIES_STAT_COUNT_RANK_ZSET, 4, Integer.MAX_VALUE);
        articleIds = redisCache.zsetRangeByScore(ArticleRedisKeyPrefix.TRAVELS_STAT_COUNT_RANK_ZSET, 4, Integer.MAX_VALUE);
        if (doStatisticNum(articleIds, -1))
            redisCache.zsetRemoveRangeByScore(ArticleRedisKeyPrefix.TRAVELS_STAT_COUNT_RANK_ZSET, 4, Integer.MAX_VALUE);
    }

    public boolean doStatisticNum(Set<Integer> articleIds, int articleType) {
        if (articleIds == null)
            return false;
        log.info("定时任务：statisticNum，正在持久化{}篇{}", articleIds.size(), articleType == 1 ? "攻略" : "游记");
        for (Integer articleId : articleIds) {
            if (articleType == 1) {
                Map<String, Integer> map = redisCache.getCacheMap(ArticleRedisKeyPrefix.STRATEGIES_STAT_DATA_MAP.getKey(articleId.toString()));
                Strategy strategy = new Strategy();
                strategy.setId(articleId.longValue());
                strategy.setViewnum(map.get("viewnum"));
                strategy.setSharenum(map.get("sharenum"));
                strategy.setReplynum(map.get("replynum"));
                strategy.setFavornum(map.get("favornum"));
                strategy.setLikesnum(map.get("likesupnum"));
                strategyMapper.updateById(strategy);
            } else {
                Map<String, Integer> map = redisCache.getCacheMap(ArticleRedisKeyPrefix.TRAVELS_STAT_DATA_MAP.getKey(articleId.toString()));
                Travel travel = new Travel();
                travel.setId(articleId.longValue());
                travel.setViewnum(map.get("viewnum"));
                travel.setSharenum(map.get("sharenum"));
                travel.setReplynum(map.get("replynum"));
                travel.setFavornum(map.get("favornum"));
                travel.setLikesnum(map.get("likesupnum"));
                travelMapper.updateById(travel);
            }
        }
        log.info("持久化完成！");
        return true;
    }

    // 每晚12点，清空点赞和浏览列表，保存收藏列表到Mongodb，可优化->TODO: 取消定时任务用户退出登录时持久化
    @Scheduled(cron = "0 0 0 * * ? ")
    public void statisticList() {
        // 获取ArticleInteractionVO对象
        Collection<String> strategyInteractionKeys = redisCache.keys(UserRedisKeyPrefix.STRATEGY_INTERACTION.getKey("*"));
        doStatisticList(strategyInteractionKeys, 1);
        Collection<String> travelInteractionKeys = redisCache.keys(UserRedisKeyPrefix.TRAVEL_INTERACTION.getKey("*"));
        doStatisticList(travelInteractionKeys, -1);
    }

    public void doStatisticList(Collection<String> keys, int articleType) {
        log.info("定时任务：statisticList，清空点赞和浏览列表");
        log.info("将{}收藏列表持久化到mongodb中", articleType == 1 ? "攻略" : "游记");
        for (String key : keys) {
            // 清空点赞和浏览列表
            UserArticleInteractionVO articleInteraction = redisCache.getCacheObject(key);
            articleInteraction.getLikesList().clear();
            articleInteraction.getViewedList().clear();
            redisCache.setCacheObject(key, articleInteraction);
            // 保存favorList到Mongodb
            String userIdStr = key.substring(key.lastIndexOf(":") + 1);
            if (articleType == 1) {
                UserStrategyFavorite strategyInteraction = new UserStrategyFavorite(userIdStr, articleInteraction.getFavoriteList());
                template.save(strategyInteraction);
            } else {
                UserTravelFavorite travelInteraction = new UserTravelFavorite(userIdStr, articleInteraction.getFavoriteList());
                template.save(travelInteraction);
            }
        }
        log.info("持久化完成！");
    }
}