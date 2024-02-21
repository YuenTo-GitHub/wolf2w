package cn.wolfcode.wolf2w.article.service.impl;

import cn.wolfcode.wolf2w.article.domain.Travel;
import cn.wolfcode.wolf2w.article.domain.TravelContent;
import cn.wolfcode.wolf2w.article.feign.IUserInfoFeignService;
import cn.wolfcode.wolf2w.article.mapper.TravelContentMapper;
import cn.wolfcode.wolf2w.article.mapper.TravelMapper;
import cn.wolfcode.wolf2w.article.query.TravelQuery;
import cn.wolfcode.wolf2w.article.redis.key.ArticleRedisKeyPrefix;
import cn.wolfcode.wolf2w.article.service.ITravelService;
import cn.wolfcode.wolf2w.article.vo.TravelRange;
import cn.wolfcode.wolf2w.auth.utils.AuthenticateUtils;
import cn.wolfcode.wolf2w.core.utils.JsonResult;
import cn.wolfcode.wolf2w.redis.utils.RedisCache;
import cn.wolfcode.wolf2w.user.dto.UserInfoDTO;
import cn.wolfcode.wolf2w.user.redis.key.UserRedisKeyPrefix;
import cn.wolfcode.wolf2w.user.vo.LoginUserVO;
import cn.wolfcode.wolf2w.user.vo.UserArticleInteractionVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;

@Service
public class TravelServiceImpl extends ServiceImpl<TravelMapper, Travel> implements ITravelService {

    private final IUserInfoFeignService userInfoFeignService;

    private final ThreadPoolExecutor businessThreadPoolExecutor;

    private final TravelContentMapper travelContentMapper;

    private final RedisCache redisCache;

    public TravelServiceImpl(@Lazy IUserInfoFeignService userInfoFeignService, ThreadPoolExecutor businessThreadPoolExecutor, TravelContentMapper travelContentMapper, RedisCache redisCache) {
        this.userInfoFeignService = userInfoFeignService;
        this.businessThreadPoolExecutor = businessThreadPoolExecutor;
        this.travelContentMapper = travelContentMapper;
        this.redisCache = redisCache;
    }

    @Override
    public Page<Travel> queryPage(TravelQuery qo) {
        Page<Travel> page = new Page<>(qo.getCurrent(), qo.getSize());
        QueryWrapper<Travel> wrapper = new QueryWrapper<>();
        wrapper.eq(qo.getDestId() != null, "dest_id", qo.getDestId());
        LoginUserVO user = AuthenticateUtils.getCurrentUser();
        if (user != null) {
            // 用户已登录，则可查看其私有的游记
            // SELECT * FROM `travel` WHERE dest_id = 355 and ((ispublic = 1 and state = 2) or author_id = 1)
            wrapper.and(w1 -> {
                w1.and(
                        w2 -> w2.eq("ispublic", Travel.ISPUBLIC_YES).eq("state", Travel.STATE_RELEASE)
                ).or().eq("author_id", user.getId());
            });
        } else {
            wrapper.eq("ispublic", Travel.ISPUBLIC_YES).eq("state", Travel.STATE_RELEASE);
        }
        TravelRange travelTimeRange = qo.getTravelTimeRange();
        TravelRange consumeRange = qo.getConsumeRange();
        TravelRange dayRange = qo.getDayRange();
        if (travelTimeRange != null) {
            wrapper.between("MONTH(travel_time)", travelTimeRange.getMin(), travelTimeRange.getMax());
        }
        if (consumeRange != null) {
            wrapper.between("avg_consume", consumeRange.getMin(), consumeRange.getMax());
        }
        if (dayRange != null) {
            wrapper.between("day", dayRange.getMin(), dayRange.getMax());
        }
        wrapper.orderByDesc(StringUtils.hasText(qo.getOrderBy()), qo.getOrderBy());
        Page<Travel> travelPage = super.page(page, wrapper);
        List<Travel> list = travelPage.getRecords();
        CountDownLatch latch = new CountDownLatch(list.size());
        for (Travel travel : list) {
            businessThreadPoolExecutor.execute(() -> {
                JsonResult<UserInfoDTO> result = userInfoFeignService.getUserInfoDTO(travel.getAuthorId());
                travel.setAuthor(result.checkAndGet());
                latch.countDown();
            });
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            latch.countDown();
        }
        return travelPage;
    }

    @Override
    public List<Travel> queryViewnumTop3(Long destId) {
        QueryWrapper<Travel> wrapper = new QueryWrapper<>();
        wrapper.eq("dest_id", destId).orderByDesc("viewnum").last("limit 3");
        return list(wrapper);
    }

    @Override
    public Travel getById(Serializable id) {
        Travel travel = super.getById(id);
        if (travel != null) {
            TravelContent travelContent = travelContentMapper.selectById(id);
            travel.setContent(travelContent);
            JsonResult<UserInfoDTO> result = userInfoFeignService.getUserInfoDTO(travel.getAuthorId());
            if (result.getCode() == JsonResult.CODE_SUCCESS) {
                travel.setAuthor(result.getData());
            }
            // 查看用户是否收藏游记
            LoginUserVO currentUser = AuthenticateUtils.getCurrentUser();
            if (currentUser != null) {
                String userIdStr = currentUser.getId().toString();
                String interactionKey = UserRedisKeyPrefix.TRAVEL_INTERACTION.getKey(userIdStr);
                UserArticleInteractionVO interaction = redisCache.getCacheObject(interactionKey);
                if (interaction.getFavoriteList().contains(id)) {
                    travel.setFavorite(true);
                }
                String key = ArticleRedisKeyPrefix.TRAVELS_STAT_DATA_MAP.getKey(id.toString());
                travel.setViewnum(redisCache.getCacheMapValue(key, "viewnum"));
                travel.setFavornum(redisCache.getCacheMapValue(key, "favornum"));
                travel.setLikesnum(redisCache.getCacheMapValue(key, "likesnum"));
                travel.setReplynum(redisCache.getCacheMapValue(key, "replynum"));
                travel.setSharenum(redisCache.getCacheMapValue(key, "sharenum"));
            }
        }
        return travel;
    }

    @Override
    public void viewnumIncr(Long tid) {
        // 用户已登录且第一次浏览此文章
        LoginUserVO currentUser = AuthenticateUtils.getCurrentUser();
        if (currentUser != null) {
            String userIdStr = currentUser.getId().toString();
            String interactionKey = UserRedisKeyPrefix.TRAVEL_INTERACTION.getKey(userIdStr);
            UserArticleInteractionVO interaction = redisCache.getCacheObject(interactionKey);
            if (interaction == null)
                interaction = new UserArticleInteractionVO();
            List<Long> viewedList = interaction.getViewedList();
            if (!viewedList.contains(tid)) {
                // 用户今天第一次浏览游记
                redisCache.hashIncrement(ArticleRedisKeyPrefix.TRAVELS_STAT_DATA_MAP, "viewnum", 1, tid.toString());
                // 记录key变动次数
                redisCache.zsetIncrement(ArticleRedisKeyPrefix.TRAVELS_STAT_COUNT_RANK_ZSET, 1, tid);
                viewedList.add(tid);
                // 浏览列表记录在redis，定期清除
                redisCache.setCacheObject(new UserRedisKeyPrefix(interactionKey), interaction);
            }
        }
    }

    @Override
    public Map<String, Integer> getStatisticData(String id) {
        return redisCache.getCacheMap(ArticleRedisKeyPrefix.TRAVELS_STAT_DATA_MAP.getKey(id));
    }

    @Override
    public List<Travel> queryDestinationName(String destName) {
        List<Travel> list = list(new QueryWrapper<Travel>().eq("dest_name", destName));
        CountDownLatch latch = new CountDownLatch(list.size());
        for (Travel travel : list) {
            businessThreadPoolExecutor.execute(() -> {
                JsonResult<UserInfoDTO> result = userInfoFeignService.getUserInfoDTO(travel.getAuthorId());
                travel.setAuthor(result.checkAndGet());
                latch.countDown();
            });
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            latch.countDown();
        }
        return list;
    }
}