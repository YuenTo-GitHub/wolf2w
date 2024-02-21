package cn.wolfcode.wolf2w.search.controller;

import cn.wolfcode.wolf2w.core.exception.BusinessException;
import cn.wolfcode.wolf2w.core.query.QueryObject;
import cn.wolfcode.wolf2w.core.utils.JsonResult;
import cn.wolfcode.wolf2w.redis.utils.RedisCache;
import cn.wolfcode.wolf2w.search.domain.DestinationEs;
import cn.wolfcode.wolf2w.search.domain.StrategyEs;
import cn.wolfcode.wolf2w.search.domain.TravelEs;
import cn.wolfcode.wolf2w.search.domain.UserInfoEs;
import cn.wolfcode.wolf2w.search.feign.IArticleFeignService;
import cn.wolfcode.wolf2w.search.feign.IUserInfoFeignService;
import cn.wolfcode.wolf2w.search.service.IElasticsearchService;
import cn.wolfcode.wolf2w.search.strategy.EsDataInitStrategy;
import org.apache.commons.beanutils.BeanUtils;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("init")
public class EsDataInitController {

    public static final Integer BATCH_INIT_NUM = 200;

    public static final String INIT_TYPE_USER = "user";
    public static final String INIT_TYPE_TRAVEL = "travel";
    public static final String INIT_TYPE_STRATEGY = "strategy";
    public static final String INIT_TYPE_DESTINATION = "destination";

    private final Map<String, EsDataInitStrategy> DATA_HANDLER_STRATEGY_MAP = new HashMap<>();

    @Value("${es.init.key}")
    private String initKey;
    private final IUserInfoFeignService userInfoFeignService;
    private final IArticleFeignService IArticleFeignService;

    private final IElasticsearchService elasticsearchService;

    private final RedisCache redisCache;

    public EsDataInitController(IUserInfoFeignService userInfoFeignService, IArticleFeignService IArticleFeignService, IElasticsearchService elasticsearchService, RedisCache redisCache) {
        this.userInfoFeignService = userInfoFeignService;
        this.IArticleFeignService = IArticleFeignService;
        this.elasticsearchService = elasticsearchService;
        this.redisCache = redisCache;
    }

    @PostConstruct
    public void postConstruct() {
        EsDataInitStrategy userDataInitStrategy
                = new EsDataInitStrategy(userInfoFeignService::userSearchList, UserInfoEs.class);
        DATA_HANDLER_STRATEGY_MAP.put(INIT_TYPE_USER, userDataInitStrategy);
        // 游记初始化
        EsDataInitStrategy travelDataInitStrategy
                = new EsDataInitStrategy(IArticleFeignService::travelSearchList, TravelEs.class);
        DATA_HANDLER_STRATEGY_MAP.put(INIT_TYPE_TRAVEL, travelDataInitStrategy);

        // 攻略初始化
        EsDataInitStrategy strategyDataInitStrategy
                = new EsDataInitStrategy(IArticleFeignService::strategySearchList, StrategyEs.class);
        DATA_HANDLER_STRATEGY_MAP.put(INIT_TYPE_STRATEGY, strategyDataInitStrategy);

        // 目的地初始化
        EsDataInitStrategy destinationDataInitStrategy
                = new EsDataInitStrategy(IArticleFeignService::destinationSearchList, DestinationEs.class);
        DATA_HANDLER_STRATEGY_MAP.put(INIT_TYPE_DESTINATION, destinationDataInitStrategy);
    }

    @GetMapping("/{initKey}/{type}")
    public ResponseEntity<?> initData(@PathVariable String initKey, @PathVariable String type) {
        if (StringUtils.isEmpty(initKey) || !this.initKey.equals(initKey)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Boolean set = redisCache.setnx("es:init:" + initKey + ":" + type, "inited");
        if (set) {
            log.info("搜索服务：{}数据开始初始化...", type);
            this.doInit(type);
            return ResponseEntity.ok().body("data init success!");
        }
        return ResponseEntity.notFound().build();
    }

    private void doInit(String type) {
        EsDataInitStrategy strategy = DATA_HANDLER_STRATEGY_MAP.get(type);
        if (strategy == null) {
            throw new BusinessException("初始化参数类型错误！");
        }
        int current = 1;
        do {
            JsonResult<List<Object>> ret = strategy.getFunction().apply(new QueryObject(current++, BATCH_INIT_NUM));
            log.info("[ES 数据初始化] 初始化开始, 查询{}数据 data={}", type, JSON.toJSONString(ret));
            List<Object> list = ret.checkAndGet();
            if (list == null || list.size() == 0) {
                break;
            }
            try {
                List<Object> dataList = new ArrayList<>(list.size());
                Class<?> clazz = strategy.getClazz();
                for (Object dto : list) {
                    Object es = clazz.newInstance();
                    BeanUtils.copyProperties(es, dto);
                    dataList.add(es);
                }
                // 批量保存数据
                elasticsearchService.save(dataList);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } while (true);
    }
}
