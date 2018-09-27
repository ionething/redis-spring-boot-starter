package io.github.ionething.springboot.redis.locks;


import io.github.ionething.springboot.redis.RedisConstants;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.IOException;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

/**
 *
 * implement the DistReentrantLock interface
 * @author Vincent
 *
 */
@Slf4j
@RequiredArgsConstructor
public class RedisReentrantLock implements DistReentrantLock, AutoCloseable {

    /**
     *
     */
    private final static String KEY_PREFIX = "lock:";

    /**
     *
     */
    public final static String SET_IF_NOT_EXIST = "NX";

    /**
     *
     */
    public final static String SET_WITH_EXPIRE_TIME = "PX";

    /**
     * 锁过期时间
     */
    private final static Long EXPIRED_TIME = 2 * 60 * 1000L;

    /**
     * 锁重试时间
     */
    private final static Long LOCK_RETRY_AWAIT = 300L;

    /**
     *
     */
    private final ConcurrentMap<Thread, LockData> threadData = new ConcurrentHashMap<>();

    /**
     * 可重入锁参数
     */
    private static class LockData {
        final Thread thread;
        final String key;
        final String value;
        final AtomicInteger count = new AtomicInteger(1);

        private LockData(Thread thread, String key, String value) {
            this.thread = thread;
            this.key = key;
            this.value = value;
        }
    }

    @NonNull
    private JedisPool jedisPool;

    @NonNull
    private String key;


    /**
     *
     * @param time
     * @param unit
     * @return
     * @throws Exception
     */
    private boolean internalLock(long time, TimeUnit unit) {
        if(StringUtils.isEmpty(key)) {
            throw new NullPointerException("key is null, please make sure key not null");
        }

        final long waitMillis = unit.toMillis(time);

        Thread currentThread = Thread.currentThread();

        LockData lockData = threadData.get(currentThread);

        if(lockData != null) {
            // 重入
            lockData.count.incrementAndGet();
            return true;
        }

        try (Jedis jedis = jedisPool.getResource()) {
            final long startMillis = System.currentTimeMillis();
            while (true) {
                String value = UUID.randomUUID().toString();
                String result = jedis.set(KEY_PREFIX + key, value,  SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, EXPIRED_TIME);
                if (RedisConstants.SET_SUCCESS.equals(result)) {
                    // 加锁成功
                    LockData newLockData = new LockData(currentThread, key, value);
                    threadData.put(currentThread, newLockData);
                    return true;
                }

                // 执行时间 > 等待时间 + 重试等待时间
                boolean timeout = waitMillis > 0 && System.currentTimeMillis() - startMillis > waitMillis + LOCK_RETRY_AWAIT;
                if (waitMillis == 0 || timeout) {
                    break;
                }

                LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(LOCK_RETRY_AWAIT));
            }
        }

        return false;
    }

    /**
     *
     */
    private void internalUnlock(String value) {
        // 如果取得的锁内容相同，就删除，否则什么也不做 (为了避免锁过期后或其他原因，删除别的锁)
        String luaScript = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.eval(luaScript, Collections.singletonList(key), Collections.singletonList(value));
        }
    }

    @Override
    public void lock() throws Exception {
        if (! internalLock(-1, TimeUnit.MILLISECONDS)) {
            throw new IOException("Lost connection while trying to acquire lock: " + key);
        }
    }

    @Override
    public boolean tryLock() {
        return tryLock(0, TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean tryLock(long timeout, TimeUnit unit) {
        return internalLock(timeout, unit);
    }

    @Override
    public void unlock() {
        Thread currentThread = Thread.currentThread();
        LockData lockData = threadData.get(currentThread);
        if ( lockData == null ) {
            throw new IllegalMonitorStateException("You do not own the lock: " + key);
        }

        int newLockCount = lockData.count.decrementAndGet();
        if ( newLockCount > 0 ) {
            return;
        }

        if ( newLockCount < 0 ) {
            throw new IllegalMonitorStateException("Lock count has gone negative for lock: " + key);
        }

        try {
            internalUnlock(lockData.value);
        } finally {
            threadData.remove(currentThread);
        }
    }

    @Override
    public void close() throws Exception {
        unlock();
    }
}
