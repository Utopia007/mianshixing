package com.luyouxiao.mianshixing.manager;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.IntegerCodec;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Instant;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * @author 鹿又笑
 * @create 2024/10/9-20:17
 * @description
 */
@Service
@Slf4j
public class CounterManager {

    @Resource
    private RedissonClient redissonClient;

    // 同一方法区分不同参数列表，增强了可用性

    /**
     * 增加并返回计数，默认统计一分钟内的统计结果
     *
     * @param key
     * @return
     */
    public long incrAndGetCounter(String key) {
        return incrAndGetCounter(key, 1, TimeUnit.MINUTES);
    }

    /**
     * @param key
     * @param timeInterval
     * @param timeUnit
     * @return
     */
    public long incrAndGetCounter(String key, int timeInterval, TimeUnit timeUnit) {
        int expirationTimeSeconds;
        switch (timeUnit) {
            case SECONDS:
                expirationTimeSeconds = timeInterval;
                break;
            case MINUTES:
                expirationTimeSeconds = timeInterval * 60;
                break;
            case HOURS:
                expirationTimeSeconds = timeInterval * 60 * 60;
                break;
            default:
                throw new IllegalArgumentException("不支持的时间单位");
        }
        return incrAndGetCounter(key, expirationTimeSeconds, timeUnit, expirationTimeSeconds);
    }

    /**
     *
     * @param key
     * @param timeInterval
     * @param timeUnit
     * @param expirationTimeSeconds
     * @return
     */
    public long incrAndGetCounter(String key, int timeInterval, TimeUnit timeUnit, long expirationTimeSeconds) {
        if (StrUtil.isBlank(key)) {
            return 0;
        }
        // 根据时间粒度生成redis key
        long timeFactor;
        switch (timeUnit) {
            case SECONDS:
                timeFactor = Instant.now().getEpochSecond() / timeInterval;
                break;
            case MINUTES:
                timeFactor = Instant.now().getEpochSecond() / timeInterval / 60;
                break;
            case HOURS:
                timeFactor = Instant.now().getEpochSecond() / timeInterval / 60 / 60;
                break;
            default:
                throw new IllegalArgumentException("不支持的时间单位");
        }
        String redisKey = key + ":" + timeFactor;
        // Lua脚本 -> 设置key、给key增加计数、给key设置过期时间
        String luaScript =
                "if redis.call('exists', KEYS[1]) == 1 then " +
                        "  return redis.call('incr', KEYS[1]); " +
                        "else " +
                        "  redis.call('set', KEYS[1], 1); " +
                        "  redis.call('expire', KEYS[1], 180); " +  // 设置 180 秒过期时间
                        "  return 1; " +
                        "end";
        // 执行Lua脚本
        RScript script = redissonClient.getScript(IntegerCodec.INSTANCE);
        Object countObj = script.eval(RScript.Mode.READ_WRITE,
                luaScript,
                RScript.ReturnType.INTEGER,
                Collections.singletonList(redisKey),
                expirationTimeSeconds);
        return Long.parseLong(countObj.toString());
    }

}
