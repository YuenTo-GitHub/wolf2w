package cn.wolfcode.wolf2w.article.listener;

import cn.wolfcode.wolf2w.article.domain.Strategy;
import cn.wolfcode.wolf2w.article.domain.Travel;
import cn.wolfcode.wolf2w.article.redis.key.ArticleRedisKeyPrefix;
import cn.wolfcode.wolf2w.article.service.IStrategyService;
import cn.wolfcode.wolf2w.article.service.ITravelService;
import cn.wolfcode.wolf2w.redis.utils.RedisCache;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class RedisStatDataInitListener implements ApplicationListener<ContextRefreshedEvent> {

    private final RedisCache redisCache;
    private final IStrategyService strategyService;

    private final ITravelService travelService;

    public RedisStatDataInitListener(IStrategyService strategyService, RedisCache redisCache, ITravelService travelService) {
        this.strategyService = strategyService;
        this.redisCache = redisCache;
        this.travelService = travelService;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext ctx = event.getApplicationContext();
        if (AnnotationConfigServletWebServerApplicationContext.class == ctx.getClass()) {
            System.out.println("----------------容器启动完成, 执行初始化数据----------------------");
            // 数据初始化
            // 1. 查询所有攻略数据
            /* TODO 不能一次加载表中所有数据, 这样的操作在项目前期是不会有问题的, 但是最终会变成一个隐藏的大bug, 到项目后期数据量大了以后就会爆发出来
                解决办法：线程池、分批加载
             */
            List<Strategy> strategies = strategyService.list();
            List<Travel> travels = travelService.list();
            // 2. 遍历攻略列表, 判断当前对象在 Redis 中是否已经存在
            for (Strategy strategy : strategies) {
                String key = ArticleRedisKeyPrefix.STRATEGIES_STAT_DATA_MAP.getKey(strategy.getId() + "");
                Boolean exists = redisCache.hasKey(key);
                if (!exists) {
                    // 3. 如果不存在, 才将数据存入 Redis
                    Map<String, Object> map = new HashMap<>();
                    map.put("viewnum", strategy.getViewnum());
                    map.put("replynum", strategy.getReplynum());
                    map.put("favornum", strategy.getFavornum());
                    map.put("sharenum", strategy.getSharenum());
                    map.put("likesnum", strategy.getLikesnum());
                    redisCache.setCacheMap(key, map);
                }
            }
            for (Travel travel : travels) {
                String key = ArticleRedisKeyPrefix.TRAVELS_STAT_DATA_MAP.getKey(travel.getId() + "");
                Boolean exists = redisCache.hasKey(key);
                if (!exists) {
                    // 3. 如果不存在, 才将数据存入 Redis
                    Map<String, Object> map = new HashMap<>();
                    map.put("viewnum", travel.getViewnum());
                    map.put("replynum", travel.getReplynum());
                    map.put("favornum", travel.getFavornum());
                    map.put("sharenum", travel.getSharenum());
                    map.put("likesnum", travel.getLikesnum());
                    redisCache.setCacheMap(key, map);
                }
            }
        }
    }
}
