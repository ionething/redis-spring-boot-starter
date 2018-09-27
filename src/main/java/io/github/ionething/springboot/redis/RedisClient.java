package io.github.ionething.springboot.redis;

import lombok.*;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@RequiredArgsConstructor
public class RedisClient {

    @Getter
    @NonNull
    private JedisPool jedisPool;

    /**
     *
     * @param key
     * @return
     */
    public String get(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.get(key);
        }
    }

    /**
     *
     * @param key
     * @param value
     * @return
     */
    public boolean set(String key, String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            return RedisConstants.SET_SUCCESS.equals(jedis.set(key, value));
        }
    }

    /**
     *
     * @param key
     * @param value
     * @return
     */
    public boolean setnx(String key, String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.setnx(key, value) > 0;
        }
    }
}
