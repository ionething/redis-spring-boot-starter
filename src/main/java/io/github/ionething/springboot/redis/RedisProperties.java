package io.github.ionething.springboot.redis;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@Data
@ConfigurationProperties(prefix = "redis")
public class RedisProperties {

    /**
     * Redis server host
     */
    private String host = "127.0.0.1";

    /**
     * Redis server port
     */
    private int port = 6379;

    /**
     * Login password of the redis server
     */
    private String password;

    /**
     * Whether to enable SSL support
     */
    private boolean ssl;

    /**
     * Redis pool
     */
    private final Pool pool = new Pool();

    /**
     * Pool properties.
     */
    @Data
    public static class Pool {
        /**
         * Maximum number of "idle" connections in the pool. Use a negative value to
         * indicate an unlimited number of idle connections.
         */
        private int maxIdle = 8;

        /**
         * Target for the minimum number of idle connections to maintain in the pool. This
         * setting only has an effect if it is positive.
         */
        private int minIdle = 0;

        /**
         * Maximum number of connections that can be allocated by the pool at a given
         * time. Use a negative value for no limit.
         */
        private int maxActive = 8;

        /**
         * Maximum amount of time a connection allocation should block before throwing an
         * exception when the pool is exhausted. Use a negative value to block
         * indefinitely.
         */
        private Duration maxWait = Duration.ofMillis(-1);
    }

}
