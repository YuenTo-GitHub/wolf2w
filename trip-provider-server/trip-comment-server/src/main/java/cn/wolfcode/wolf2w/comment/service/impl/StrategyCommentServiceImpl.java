package cn.wolfcode.wolf2w.comment.service.impl;

import cn.wolfcode.wolf2w.article.redis.key.ArticleRedisKeyPrefix;
import cn.wolfcode.wolf2w.auth.utils.AuthenticateUtils;
import cn.wolfcode.wolf2w.comment.domain.StrategyComment;
import cn.wolfcode.wolf2w.comment.query.CommentQuery;
import cn.wolfcode.wolf2w.comment.redis.key.StrategiesCommentRedisKeyPrefix;
import cn.wolfcode.wolf2w.comment.repository.IStrategyCommentRepository;
import cn.wolfcode.wolf2w.comment.service.IStrategyCommentService;
import cn.wolfcode.wolf2w.user.vo.LoginUserVO;
import cn.wolfcode.wolf2w.redis.utils.RedisCache;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class StrategyCommentServiceImpl implements IStrategyCommentService {

    private final IStrategyCommentRepository repository;

    private final RedisCache redisCache;

    private final MongoTemplate template;

    public StrategyCommentServiceImpl(IStrategyCommentRepository repository, RedisCache redisCache, MongoTemplate template) {
        this.repository = repository;
        this.redisCache = redisCache;
        this.template = template;
    }


    @Override
    public void save(StrategyComment comment) {
        // 新增评论
        LoginUserVO user = AuthenticateUtils.getCurrentUser();
        comment.setUserId(user.getId());
        comment.setNickname(user.getNickname());
        comment.setCity(user.getCity());
        comment.setLevel(user.getLevel());
        comment.setHeadImgUrl(user.getHeadImgUrl());
        comment.setCreateTime(new Date());
        repository.save(comment);
    }

    @Override
    public Page<StrategyComment> queryPage(CommentQuery qo) {
        Criteria criteria = Criteria.where("strategyId").is(qo.getArticleId());
        Query query = new Query();
        query.addCriteria(criteria);
        long total = template.count(query, StrategyComment.class);
        if (total == 0) {
            return Page.empty();
        }
        // 设置分页参数
        PageRequest request = PageRequest.of(qo.getCurrent() - 1, qo.getSize());
        query.skip(request.getOffset()).limit(qo.getSize());
        query.with(Sort.by(Sort.Direction.DESC, "createTime;"));
        List<StrategyComment> list = template.find(query, StrategyComment.class);
        return new PageImpl<>(list, request, total);
    }

    @Override
    public void doLike(String cid) {
        // 评论点赞
        Optional<StrategyComment> optional = repository.findById(cid);
        if (optional.isPresent()) {
            StrategyComment strategyComment = optional.get();
            LoginUserVO currentUser = AuthenticateUtils.getCurrentUser();
            Long userId = currentUser.getId();
            // 判断用户是否点过赞
            List<Long> likesList = strategyComment.getLikesList();
            if (likesList.contains(userId)) {
                // 点过赞，则取消赞，赞数减一
                strategyComment.setLikesnum(strategyComment.getLikesnum() - 1);
                likesList.remove(userId);
            } else {
                strategyComment.setLikesnum(strategyComment.getLikesnum() + 1);
                likesList.add(userId);
            }
            repository.save(strategyComment);
        }
    }

    @Override
    public void replynumIncr(Long sid) {
        // 记录key变动次数
        redisCache.zsetIncrement(ArticleRedisKeyPrefix.STRATEGIES_STAT_COUNT_RANK_ZSET, 1, sid);
        redisCache.hashIncrement(StrategiesCommentRedisKeyPrefix.STRATEGIES_STAT_DATA_MAP, "replynum", 1, sid.toString());
    }
}