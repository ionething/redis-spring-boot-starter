package io.github.ionething.springboot.redis.limit;

import io.github.ionething.springboot.redis.annotation.RedisCommonLimit;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.util.StringUtils;

@Slf4j
@RequiredArgsConstructor
public class RedisCommonLimitMethodInterceptor implements MethodInterceptor {

    @NonNull
    private RedisLimit redisLimit;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        if (redisLimit != null) {
            RedisCommonLimit redisCommonLimit = invocation.getMethod().getAnnotation(RedisCommonLimit.class);
            if (redisCommonLimit != null) {
                String name = redisCommonLimit.name();
                if (StringUtils.isEmpty(name)) {
                    name = invocation.getMethod().getName();
                }
                boolean pass = redisLimit.limit(name, redisCommonLimit.permits(), redisCommonLimit.seconds());
                if (!pass) {
                    throw new OverLimitException("the total number of invoking this method is over the limit");
                }

            }
        } else {
            log.warn("use limit but it is not effective");
        }
        return invocation.proceed();
    }
}
