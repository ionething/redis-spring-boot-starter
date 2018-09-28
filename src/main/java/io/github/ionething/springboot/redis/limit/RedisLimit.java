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
            String result = jedis.eval(limitScript, Collections.singletonList(LIMIT_PREFIX + name), Collections.singletonList(String.valueOf(permits))).toString();
            if (FAiLURE_CODE.equals(result)) {
                return false;
            } else {
                return true;
            }
        }
    }

    private static final String limitScript = "" +
            "local key = KEYS[1]\n" +
            "local permits = tonumber(ARGV[1])\n" +
            "local current = tonumber(redis.call('get', key) or '0')\n" +
            "if current + 1 > permits then\n" +
            "return 0\n" +
            "else\n" +
            "redis.call('INCRBY', key, 1)\n" +
            "redis.call('EXPIRE', key, 3)\n" +
            "return current + 1\n" +
            "end\n";
}
