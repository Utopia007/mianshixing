package com.luyouxiao.mianshixing.constant;

/**
 * @author 鹿又笑
 * @create 2024/9/21-15:55
 * @description
 */
public interface RedisConstant {

    String USER_SIGN_IN_REDIS_KEY_PREFIX = "user:signs";

    /**
     * 获取用户签到记录的key
     * @param year
     * @param userId
     * @return 拼接好的 redis key
     */
    static String getUserSignInRedisKey(int year, long userId) {
        return String.format("%s:%d:%d", USER_SIGN_IN_REDIS_KEY_PREFIX, year, userId);
    }

}
