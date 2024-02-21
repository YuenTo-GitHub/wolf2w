package cn.wolfcode.wolf2w.comment.service.impl;

import cn.wolfcode.wolf2w.article.redis.key.ArticleRedisKeyPrefix;
import cn.wolfcode.wolf2w.auth.utils.AuthenticateUtils;
import cn.wolfcode.wolf2w.comment.domain.TravelComment;
import cn.wolfcode.wolf2w.comment.query.CommentQuery;
import cn.wolfcode.wolf2w.comment.redis.key.TravelsCommentRedisKeyPrefix;
import cn.wolfcode.wolf2w.comment.repository.ITravelCommentRepository;
import cn.wolfcode.wolf2w.comment.service.ITravelCommentService;
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
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class TravelCommentServiceImpl implements ITravelCommentService {

    private final ITravelCommentRepository repository;

    private final MongoTemplate template;

    private final RedisCache redisCache;

    public TravelCommentServiceImpl(ITravelCommentRepository repository, MongoTemplate template, RedisCache redisCache) {
        this.repository = repository;
        this.template = template;
        this.redisCache = redisCache;
    }

    @Override
    public void save(TravelComment comment) {
        LoginUserVO user = AuthenticateUtils.getCurrentUser();
        comment.setUserId(user.getId());
        comment.setNickname(user.getNickname());
        comment.setCity(user.getCity());
        comment.setLevel(user.getLevel());
        comment.setHeadImgUrl(user.getHeadImgUrl());
        comment.setCreateTime(new Date());
        //维护评论的评论（关联评论）
        TravelComment refComment = comment.getRefComment();
        if (refComment != null && StringUtils.hasLength(refComment.getId())) {
            //当前添加的评论是评论的回复
            comment.setType(TravelComment.TRAVEL_COMMENT_TYPE_REPLY);
        } else {
            //当前添加的普通评论
            comment.setType(TravelComment.TRAVEL_COMMENT_TYPE_COMMON);
        }
        repository.save(comment);
    }

    @Override
    public List<TravelComment> queryByTravelId(Long travelId) {
        Query query = new Query().with(Sort.by(Sort.Direction.DESC, "createTime"))
                .addCriteria(Criteria.where("travelId").is(travelId));
        List<TravelComment> comments = template.find(query, TravelComment.class);
        for (TravelComment comment : comments) {
            TravelComment refComment = comment.getRefComment();
            String refId;
            if (refComment != null && StringUtils.hasLength((refId = refComment.getId()))) {
                Optional<TravelComment> refCommentOptional = repository.findById(refId);
                comment.setRefComment(refCommentOptional.orElse(null));
            }
        }
        return comments;
    }

    @Override
    public Page<TravelComment> queryPage(CommentQuery qo) {
        Criteria criteria = Criteria.where("travelId").is(qo.getArticleId());

        Query query = new Query();
        query.addCriteria(criteria);
        long total = template.count(query, TravelComment.class);
        if (total == 0) {
            return Page.empty();
        }
        // 设置分页参数
        PageRequest request = PageRequest.of(qo.getCurrent() - 1, qo.getSize());
        query.skip(request.getOffset()).limit(qo.getSize());
        query.with(Sort.by(Sort.Direction.DESC, "createTime;"));
        List<TravelComment> list = template.find(query, TravelComment.class);
        return new PageImpl<>(list, request, total);
    }

    @Override
    public void replynumIncr(Long tid) {
        redisCache.zsetIncrement(ArticleRedisKeyPrefix.TRAVELS_STAT_COUNT_RANK_ZSET, 1, tid);
        redisCache.hashIncrement(TravelsCommentRedisKeyPrefix.TRAVELS_STAT_DATA_MAP, "replynum", 1, tid.toString());
    }
}
