package io.github.ionething.springboot.redis.limit;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Collections;

@Slf4j
@RequiredArgsConstructor
public class RedisLimit {

    private static final String LIMIT_PREFIX = "limit:";

    private static final String FAiLURE_CODE = "0";

    @NonNull
    private JedisPool jedisPool;

    public boolean limit(String name, int permits) {
        try(Jedis jedis = jedisPool.getResource()) {
            String result = (String) jedis.eval(limitScript, Collections.singletonList(LIMIT_PREFIX + name), Collections.singletonList(String.valueOf(permits)));
            if (FAiLURE_CODE.equals(result)) {
                return false;
            } else {
                return true;
            }
        }

    }

    private static final String limitScript = "" +
            "local key = KEYS[1]" +
            "local permits = tonumber(ARGV[1])" +
            "local current = tonumber(redis.call('get', key) or '0')" +
            "if current + 1 > permits then " +
            "return 0;" +
            "else" +
            "redis.call('INCRBY', key, 1)" +
            "redis.call('EXPIRE', key, 3)" +
            "return current + 1" +
            "end";
}
