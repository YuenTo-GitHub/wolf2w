package cn.wolfcode.wolf2w.user.redis.key;

import cn.wolfcode.wolf2w.redis.key.BaseKeyPrefix;

public class UserRedisKeyPrefix extends BaseKeyPrefix {

    public static final UserRedisKeyPrefix STRATEGY_INTERACTION = new UserRedisKeyPrefix("USERS:STRATEGY:INTERACTION");
    public static final UserRedisKeyPrefix TRAVEL_INTERACTION = new UserRedisKeyPrefix("USERS:TRAVEL:INTERACTION");
    public static final UserRedisKeyPrefix USER_REGISTER_VERIFY_CODE_STRING = new UserRedisKeyPrefix("USERS:REGISTER:VERIFY_CODE");
    public static final UserRedisKeyPrefix USER_LOGIN_VERIFY_CODE_STRING = new UserRedisKeyPrefix("USERS:LOGIN:VERIFY_CODE");
    public static final UserRedisKeyPrefix USER_LOGIN_INFO_STRING = new UserRedisKeyPrefix("USERS:LOGIN:INFO");

    public UserRedisKeyPrefix(String prefix) {
        super(prefix);
    }
}
