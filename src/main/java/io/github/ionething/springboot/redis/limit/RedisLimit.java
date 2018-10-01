package io.github.ionething.springboot.redis.limit;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class RedisLimit {

    private static final String LIMIT_PREFIX = "limit:";

    private static final String FAILURE_CODE = "0";

    @NonNull
    private JedisPool jedisPool;

    public boolean limit(String name, int permits, int seconds) {
        try(Jedis jedis = jedisPool.getResource()) {
            List<String> keys = Collections.singletonList(LIMIT_PREFIX + name);
            List<String> args = Arrays.asList(new String[]{String.valueOf(permits), String.valueOf(seconds)});
            String result = jedis.eval(limitScript, keys, args).toString();
            if (FAILURE_CODE.equals(result)) {
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
            "redis.call('EXPIRE', key, ARGV[2])\n" +
            "return current + 1\n" +
            "end\n";
}
