package cn.wolfcode.wolf2w.article.service.impl;

import cn.wolfcode.wolf2w.article.domain.*;
import cn.wolfcode.wolf2w.article.mapper.StrategyContentMapper;
import cn.wolfcode.wolf2w.article.mapper.StrategyMapper;
import cn.wolfcode.wolf2w.article.query.StrategyQuery;
import cn.wolfcode.wolf2w.article.redis.key.ArticleRedisKeyPrefix;
import cn.wolfcode.wolf2w.article.service.IDestinationService;
import cn.wolfcode.wolf2w.article.service.IStrategyCatalogService;
import cn.wolfcode.wolf2w.article.service.IStrategyService;
import cn.wolfcode.wolf2w.article.service.IStrategyThemeService;
import cn.wolfcode.wolf2w.article.utils.UploadUtils;
import cn.wolfcode.wolf2w.article.vo.StrategyCondition;
import cn.wolfcode.wolf2w.auth.utils.AuthenticateUtils;
import cn.wolfcode.wolf2w.core.exception.BusinessException;
import cn.wolfcode.wolf2w.redis.utils.RedisCache;
import cn.wolfcode.wolf2w.user.redis.key.UserRedisKeyPrefix;
import cn.wolfcode.wolf2w.user.vo.LoginUserVO;
import cn.wolfcode.wolf2w.user.vo.UserArticleInteractionVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class StrategyServiceImpl extends ServiceImpl<StrategyMapper, Strategy> implements IStrategyService {

    private final IStrategyThemeService strategyThemeService;
    private final IDestinationService destinationService;
    private final IStrategyCatalogService strategyCatalogService;

    private final StrategyContentMapper strategyContentMapper;

    private final RedisCache redisCache;

    public StrategyServiceImpl(IStrategyCatalogService strategyCatalogService, IDestinationService destinationService,
                               IStrategyThemeService strategyThemeService, StrategyContentMapper strategyContentMapper1, RedisCache redisCache) {
        this.strategyCatalogService = strategyCatalogService;
        this.destinationService = destinationService;
        this.strategyThemeService = strategyThemeService;
        this.strategyContentMapper = strategyContentMapper1;
        this.redisCache = redisCache;
    }

    @Override
    public List<StrategyCatalog> findGroupsByDestId(Long destId) {
        return getBaseMapper().selectGroupsByDestId(destId);
    }

    @Override
    public Page<Strategy> queryPage(StrategyQuery qo) {
        if (qo.getType() != null && qo.getType() > 0 && qo.getRefid() != null && qo.getRefid() > 0) {
            if (qo.getType() == StrategyQuery.TYPE_THEME) {
                qo.setThemeId(qo.getRefid());
            } else {
                qo.setDestId(qo.getRefid());
            }
        }
        Page<Strategy> page = new Page<>(qo.getCurrent(), qo.getSize());
        QueryWrapper<Strategy> wrapper = new QueryWrapper<>();
        wrapper.like(StringUtils.hasText(qo.getKeyword()), "sub_title", qo.getKeyword())
                .eq(qo.getDestId() != null, "dest_id", qo.getDestId())
                .eq(qo.getThemeId() != null, "theme_id", qo.getThemeId())
                .eq( "state", Strategy.STATE_PUBLISH)
                .orderByDesc(StringUtils.hasText(qo.getOrderBy()), qo.getOrderBy());
        return page(page, wrapper);
    }

    @Override
    public StrategyContent getContentById(Long id) {
        return strategyContentMapper.selectById(id);
    }

    // 查询指定目的地下浏览量前三的攻略
    @Override
    public List<Strategy> queryViewnumTop3(Long destId) {
        QueryWrapper<Strategy> wrapper = new QueryWrapper<>();
        wrapper.eq("dest_id", destId).orderByDesc("viewnum").last("limit 3");
        return list(wrapper);
    }

    @Override
    public List<StrategyCondition> findDestCondition(int abroad) {
        return getBaseMapper().selectDestCondition(abroad);
    }

    @Override
    public List<StrategyCondition> findThemeCondition() {
        return getBaseMapper().selectThemeCondition();
    }

    @Override
    public void viewnumIncr(Long sid) {
        // 用户已登录且第一次浏览此文章
        LoginUserVO currentUser = AuthenticateUtils.getCurrentUser();
        if (currentUser != null) {
            String userIdStr = currentUser.getId().toString();
            String interactionKey = UserRedisKeyPrefix.STRATEGY_INTERACTION.getKey(userIdStr);
            UserArticleInteractionVO interaction = redisCache.getCacheObject(interactionKey);
            if (interaction == null)
                interaction = new UserArticleInteractionVO();
            List<Long> viewedList = interaction.getViewedList();
            if (!viewedList.contains(sid)) {
                // 用户今天第一次浏览攻略
                redisCache.hashIncrement(ArticleRedisKeyPrefix.STRATEGIES_STAT_DATA_MAP, "viewnum", 1, sid.toString());
                redisCache.zsetIncrement(ArticleRedisKeyPrefix.STRATEGIES_STAT_COUNT_RANK_ZSET, 1, sid);
                viewedList.add(sid);
                // 浏览列表记录在redis，定期清除
                redisCache.setCacheObject(new UserRedisKeyPrefix(interactionKey), interaction);
            }
        }
    }

    @Override
    public Map<String, Integer> getStatisticData(String id) {
        return redisCache.getCacheMap(ArticleRedisKeyPrefix.STRATEGIES_STAT_DATA_MAP.getKey(id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(Strategy strategy) {
        return doSaveOrUpdate(strategy);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(Strategy strategy) {
        return doSaveOrUpdate(strategy);
    }

    public boolean doSaveOrUpdate(Strategy strategy) {
        if (!StringUtils.isEmpty(strategy.getCoverUrl()) || !strategy.getCoverUrl().startsWith("http")) {
            // 1，完成封面图片的上传
            String fileName = UUID.randomUUID().toString();
            String url = UploadUtils.uploadImgByBase64("images/strategies", fileName + ".jpg", strategy.getCoverUrl());
            strategy.setCoverUrl(url);
        }
        StrategyCatalog strategyCatalog = strategyCatalogService.getById(strategy.getCatalogId());
        if (strategyCatalog == null) {
            throw new BusinessException("错误！主题分类为空");
        } else {
            // 2. 补充分类名称
            strategy.setCatalogName(strategyCatalog.getName());
            // 3. 设置目的地id和名称
            strategy.setDestId(strategyCatalog.getDestId());
            strategy.setDestName(strategyCatalog.getDestName());
            // 4. 是否是国外
            List<Destination> toasts = destinationService.queryToast(strategyCatalog.getDestId());
            if (toasts.get(0).getId() == 1) {
                strategy.setIsabroad(Strategy.ABROAD_NO);
            } else {
                strategy.setIsabroad(Strategy.ABROAD_YES);
            }
        }
        // 5. 查询并设置主题名称
        StrategyTheme theme = strategyThemeService.getById(strategy.getThemeId());
        strategy.setThemeName(theme.getName());
        // 新增操作
        if (strategy.getId() == null) {
            // 6. 设置创建时间
            strategy.setCreateTime(new Date());
            // 7. 重新设置状态
            strategy.setState(Strategy.STATE_NORMAL);
            // viewnum等数值默认为0
            // 8. 保存攻略对象，得到攻略自增id
            boolean saved = super.save(strategy);
            // 9. 将攻略id保存到内容对象中，并保存内容对象
            StrategyContent content = strategy.getContent();
            content.setId(strategy.getId());
            return saved && strategyContentMapper.insert(content) > 0;
        }
        // 修改操作
        boolean updated = super.updateById(strategy);
        StrategyContent content = strategy.getContent();
        content.setId(strategy.getId());
        return updated && strategyContentMapper.updateById(content) > 0;
    }

    @Override
    public Strategy getById(Serializable id) {
        Strategy strategy = super.getById(id);
        if (strategy != null) {
            StrategyContent content = strategyContentMapper.selectById(id);
            strategy.setContent(content);
            // 查看用户是否收藏攻略
            LoginUserVO currentUser = AuthenticateUtils.getCurrentUser();
            if (currentUser != null) {
                String userIdStr = currentUser.getId().toString();
                String interactionKey = UserRedisKeyPrefix.STRATEGY_INTERACTION.getKey(userIdStr);
                UserArticleInteractionVO interaction = redisCache.getCacheObject(interactionKey);
                if (interaction.getFavoriteList().contains(id)) {
                    strategy.setFavorite(true);
                }
                String key = ArticleRedisKeyPrefix.STRATEGIES_STAT_DATA_MAP.getKey(id.toString());
                strategy.setViewnum(redisCache.getCacheMapValue(key, "viewnum"));
                strategy.setFavornum(redisCache.getCacheMapValue(key, "favornum"));
                strategy.setLikesnum(redisCache.getCacheMapValue(key, "likesnum"));
                strategy.setReplynum(redisCache.getCacheMapValue(key, "replynum"));
                strategy.setSharenum(redisCache.getCacheMapValue(key, "sharenum"));
            }
        }
        return strategy;
    }
}