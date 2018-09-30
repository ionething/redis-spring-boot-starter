package io.github.ionething.springboot.redis.limit;

/**
 * 次数超过限制异常
 * @author Vincent
 *
 */
public class OverLimitException extends RuntimeException {

    public OverLimitException(String msg) {
        super(msg);
    }

}
