package io.github.ionething.springboot.redis.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RedisCommonLimit {

    /**
     * 名称，相同名称采取统一策略
     * @return
     */
    String name() default "";

    /**
     * 允许访问次数
     * @return
     */
    int permits() default 1;

    /**
     * 时间内
     * @return
     */
    int seconds() default 1;

}
