package io.github.ionething.springboot.redis.limit;

/**
 *
 * @author Vincent
 *
 */
public class OverLimitException extends RuntimeException {

    public OverLimitException(String msg) {
        super(msg);
    }

}
