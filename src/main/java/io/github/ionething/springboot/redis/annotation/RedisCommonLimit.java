package io.github.ionething.springboot.redis.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RedisCommonLimit {

    String name() default "";

    /**
     *
     * @return
     */
    int permits() default 1;

}
