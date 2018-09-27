package io.github.ionething.springboot.redis;

import io.github.ionething.springboot.redis.limit.RedisLimit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

/**
 * AutoConfigure
 */
@Configuration
@EnableConfigurationProperties(RedisProperties.class)
@ConditionalOnClass(RedisClient.class)
public class RedisAutoConfiguration {

    @Autowired
    private RedisProperties redisProperties;

    @Bean(name = "jedisPool")
    public JedisPool jedisPool() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(redisProperties.getPool().getMaxActive());
        config.setMaxIdle(redisProperties.getPool().getMaxIdle());
        config.setMinIdle(redisProperties.getPool().getMinIdle());
        if (redisProperties.getPool().getMaxWait() != null) {
            config.setMaxWaitMillis(redisProperties.getPool().getMaxWait().toMillis());
        }
        JedisPool jedisPool = null;
        if (StringUtils.isEmpty(redisProperties.getPassword())) {
            jedisPool = new JedisPool(config, redisProperties.getHost(), redisProperties.getPort());
        } else {
            jedisPool = new JedisPool(config, redisProperties.getHost(), redisProperties.getPort(), Protocol.DEFAULT_TIMEOUT, redisProperties.getPassword());
        }
        return jedisPool;
    }

    @Bean
    @ConditionalOnMissingBean(RedisClient.class)
    public RedisClient redisClient(@Autowired @Qualifier(value = "jedisPool") JedisPool jedisPool) {
        return new RedisClient(jedisPool);
    }

    @Bean
    @ConditionalOnProperty(prefix = "redis.limit", name = "ignore", havingValue = "false", matchIfMissing = true)
    public RedisLimit redisLimit(@Autowired @Qualifier(value = "jedisPool") JedisPool jedisPool) {
        return redisLimit(jedisPool);
    }

}
