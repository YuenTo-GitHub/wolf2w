package cn.wolfcode.wolf2w.user.service.impl;

import cn.wolfcode.wolf2w.article.redis.key.ArticleRedisKeyPrefix;
import cn.wolfcode.wolf2w.auth.service.impl.JwtServiceImpl;
import cn.wolfcode.wolf2w.auth.utils.AuthenticateUtils;
import cn.wolfcode.wolf2w.core.exception.BusinessException;
import cn.wolfcode.wolf2w.core.utils.JsonResult;
import cn.wolfcode.wolf2w.core.utils.MD5Utils;
import cn.wolfcode.wolf2w.redis.utils.RedisCache;
import cn.wolfcode.wolf2w.user.domain.UserInfo;
import cn.wolfcode.wolf2w.user.domain.UserStrategyFavorite;
import cn.wolfcode.wolf2w.user.domain.UserTravelFavorite;
import cn.wolfcode.wolf2w.user.dto.UserInfoDTO;
import cn.wolfcode.wolf2w.user.mapper.UserInfoMapper;
import cn.wolfcode.wolf2w.user.redis.key.UserRedisKeyPrefix;
import cn.wolfcode.wolf2w.user.repository.IStrategyInteractionRepository;
import cn.wolfcode.wolf2w.user.repository.ITravelInteractionRepository;
import cn.wolfcode.wolf2w.user.service.IUserInfoService;
import cn.wolfcode.wolf2w.user.vo.LoginUserVO;
import cn.wolfcode.wolf2w.user.vo.UserArticleInteractionVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements IUserInfoService {

    private final RedisCache redisCache;

    private final JwtServiceImpl jwtServiceImpl;

    private final IStrategyInteractionRepository strategyInteractionRepository;

    private final ITravelInteractionRepository travelInteractionRepository;

    public UserInfoServiceImpl(RedisCache redisCache, JwtServiceImpl jwtServiceImpl, IStrategyInteractionRepository strategyInteractionRepository, ITravelInteractionRepository travelInteractionRepository) {
        this.redisCache = redisCache;
        this.jwtServiceImpl = jwtServiceImpl;
        this.strategyInteractionRepository = strategyInteractionRepository;
        this.travelInteractionRepository = travelInteractionRepository;
    }

    @Override
    public void register(String phone, String nickname, String password, String rpassword, String verifyCode) {
        //判断手机号码是否唯一
        if (this.queryByPhone(phone) != null) {
            throw new BusinessException(JsonResult.CODE_REGISTER_ERROR, "手机号码已经被注册了！");
        }
        //判断验证码是否一致
        String key = UserRedisKeyPrefix.USER_REGISTER_VERIFY_CODE_STRING.getKey(phone);
        String code = redisCache.getCacheObject(key);
        if (!verifyCode.equals(code)) {
            throw new BusinessException(JsonResult.CODE_SMS_ERROR, "验证码错误");
        }
        // 从redis中删除验证码
        redisCache.deleteObject(key);

        //实现注册
        UserInfo userInfo = new UserInfo();
        userInfo.setNickname(nickname);
        userInfo.setPhone(phone);
        // 密码加密，手机号作为盐
        String encryptPwd = MD5Utils.getMD5(password + phone);
        userInfo.setPassword(encryptPwd);
        userInfo.setHeadImgUrl("/images/default.jpg");

        //重要属性自己控制
        userInfo.setState(UserInfo.STATE_NORMAL);
        super.save(userInfo);
    }

    @Override
    public Map<String, Object> login(String phone, String password, HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        String encryptPwd = MD5Utils.getMD5(password + phone);
        //查询用户对象
        QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("phone", phone);
        wrapper.eq("password", encryptPwd);
        UserInfo user = super.getOne(wrapper);
        if (user == null) {
            map.put("errorMsg", "账号或密码错误");
            return map;
        } else if (user.getState() != UserInfo.STATE_NORMAL) {
            map.put("errorMsg", "账号异常，请联系管理员");
            return map;
        }
        // token过期时间
        long timeOut = jwtServiceImpl.getJwtConfig().getTimeOut();

        // 将用户信息存入redis
        LoginUserVO loginUser = new LoginUserVO();
        BeanUtils.copyProperties(user, loginUser);
        long now = System.currentTimeMillis();
        long expireTime = now + timeOut;
        loginUser.setLoginTime(now);
        loginUser.setExpireTime(expireTime);
        UserRedisKeyPrefix loginKeyPrefix = UserRedisKeyPrefix.USER_LOGIN_INFO_STRING;
        loginKeyPrefix.setTimeout(timeOut);
        loginKeyPrefix.setUnit(TimeUnit.MILLISECONDS);
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        redisCache.setCacheObject(loginKeyPrefix, loginUser, uuid);
        // 生成token
        String token = jwtServiceImpl.createJwtToken(uuid);

        map.put("token", token);
        map.put("user", loginUser);
        // 获取用户点赞/收藏、浏览过的文章id列表
        String userIdStr = user.getId().toString();
        UserStrategyFavorite strategyFavorite = strategyInteractionRepository.findById(userIdStr).orElseGet(UserStrategyFavorite::new);
        UserTravelFavorite travelFavorite = travelInteractionRepository.findById(userIdStr).orElseGet(UserTravelFavorite::new);
        UserArticleInteractionVO strategyInteractionVO = new UserArticleInteractionVO();
        UserArticleInteractionVO travelInteractionVO = new UserArticleInteractionVO();
        strategyInteractionVO.setFavoriteList(strategyFavorite.getFavoriteList());
        travelInteractionVO.setFavoriteList(travelFavorite.getFavoriteList());
        // 将interactionVO对象表存入redis
        UserRedisKeyPrefix strategyInteractionKeyPrefix = UserRedisKeyPrefix.STRATEGY_INTERACTION;
        UserRedisKeyPrefix travelInteractionKeyPrefix = UserRedisKeyPrefix.TRAVEL_INTERACTION;
        // 防止用户重新登录时数据未写回mongodb或在用户登录失效/退出登录时写回mongodb
        if (!redisCache.hasKey(UserRedisKeyPrefix.STRATEGY_INTERACTION.getKey(userIdStr)))
            redisCache.setCacheObject(strategyInteractionKeyPrefix, strategyInteractionVO, userIdStr);
        if (!redisCache.hasKey(UserRedisKeyPrefix.TRAVEL_INTERACTION.getKey(userIdStr)))
            redisCache.setCacheObject(travelInteractionKeyPrefix, travelInteractionVO, userIdStr);
        return map;
    }

    @Override
    public List<UserInfo> queryByDestinationName(String destName) {
        return super.list(new QueryWrapper<UserInfo>().eq("city", destName));
    }

    @Override
    public UserInfo queryByPhone(String phone) {
        QueryWrapper<UserInfo> wrapper = new QueryWrapper<UserInfo>().eq("phone", phone);
        return getOne(wrapper);
    }

    @Override
    public UserInfoDTO getDTOById(Long id) {
        UserInfo userInfo = getById(id);
        if (userInfo != null) {
            UserInfoDTO dto = new UserInfoDTO();
            BeanUtils.copyProperties(userInfo, dto);
            return dto;
        }
        return null;
    }

    @Override
    public Boolean articleInteract(Long articleId, int type, String field) {
        if (type != -1 && type != 1) {
            // 错误的类型
            return Boolean.FALSE;
        }
        String userId = AuthenticateUtils.getCurrentUser().getId().toString();
        if (type == 1) {
            // 攻略
            String key = UserRedisKeyPrefix.STRATEGY_INTERACTION.getKey(userId);
            redisCache.zsetIncrement(ArticleRedisKeyPrefix.STRATEGIES_STAT_COUNT_RANK_ZSET, 1, articleId);
            return doInteract(key, ArticleRedisKeyPrefix.STRATEGIES_STAT_DATA_MAP, field, articleId);
        } else {
            // 游记
            String key = UserRedisKeyPrefix.TRAVEL_INTERACTION.getKey(userId);
            redisCache.zsetIncrement(ArticleRedisKeyPrefix.TRAVELS_STAT_COUNT_RANK_ZSET, 1, articleId);
            return doInteract(key, ArticleRedisKeyPrefix.TRAVELS_STAT_DATA_MAP, field, articleId);
        }
    }

    private Boolean doInteract(String interactionKey, ArticleRedisKeyPrefix increaseKeyPrefix, String field, Long articleId) {
        UserArticleInteractionVO interaction = redisCache.getCacheObject(interactionKey);
        List<Long> list = "likes".equals(field) ? interaction.getLikesList() : interaction.getFavoriteList();
        if (list.contains(articleId)) {
            // 点过赞了/已收藏
            if ("favor".equals(field)) {
                // 取消收藏
                list.remove(articleId);
                redisCache.setCacheObject(interactionKey, interaction);
                redisCache.hashIncrement(increaseKeyPrefix, field + "num", -1, articleId.toString());
            }
            return Boolean.FALSE;
        } else {
            // 点赞/收藏
            list.add(articleId);
            redisCache.setCacheObject(interactionKey, interaction);
            redisCache.hashIncrement(increaseKeyPrefix, field + "num", 1, articleId.toString());
            return Boolean.TRUE;
        }
    }
}