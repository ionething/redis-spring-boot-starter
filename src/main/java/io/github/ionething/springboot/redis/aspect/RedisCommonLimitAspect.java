package io.github.ionething.springboot.redis.aspect;

import io.github.ionething.springboot.redis.annotation.RedisCommonLimit;
import io.github.ionething.springboot.redis.limit.RedisLimit;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;


@Slf4j
@Aspect
@Component
public class RedisCommonLimitAspect {

    @Autowired
    private RedisLimit redisLimit;

    @Pointcut("@annotation(io.github.ionething.springboot.redis.annotation.RedisCommonLimit)")
    private void pointcut(){
    }

    @Before("pointcut()")
    public void doBefore(JoinPoint joinPoint) throws Exception {

        if (redisLimit != null) {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            RedisCommonLimit redisCommonLimit = signature.getMethod().getAnnotation(RedisCommonLimit.class);
            if (redisCommonLimit != null) {
                String name = redisCommonLimit.name();
                if (StringUtils.isEmpty(name)) {
                    name = joinPoint.toLongString();
                }
                boolean pass = redisLimit.limit(name, redisCommonLimit.permits());
                if (!pass) {
                    throw new RuntimeException("the total number of invoking this method is over the limit");
                }

            }
        } else {
            log.warn("use limit but config is set to ignore it");
        }

    }

}
