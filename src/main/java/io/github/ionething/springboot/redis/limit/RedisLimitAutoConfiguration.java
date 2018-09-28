package io.github.ionething.springboot.redis.limit;

import io.github.ionething.springboot.redis.annotation.RedisCommonLimit;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;


/**
 * @author Vincent
 */
@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "redis.limit", name = "ignore", havingValue = "false", matchIfMissing = true)
public class RedisLimitAutoConfiguration extends AbstractPointcutAdvisor {

    private Pointcut pointcut;

    private Advice advice;

    @Autowired
    private RedisLimit redisLimit;

    @PostConstruct
    public void init() {
        this.pointcut = new AnnotationMatchingPointcut(null, RedisCommonLimit.class);
        this.advice = new RedisCommonLimitMethodInterceptor(redisLimit);
        log.info("init RedisLimitAutoConfiguration finish");
    }

    @Override
    public Pointcut getPointcut() {
        return pointcut;
    }

    @Override
    public Advice getAdvice() {
        return advice;
    }
}
