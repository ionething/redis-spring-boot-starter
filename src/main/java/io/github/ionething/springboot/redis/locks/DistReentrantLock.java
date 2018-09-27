package io.github.ionething.springboot.redis.locks;

import java.util.concurrent.TimeUnit;

public interface DistReentrantLock {

    void lock() throws Exception;

    boolean tryLock() throws Exception;

    boolean tryLock(long timeout, TimeUnit unit) throws Exception;

    void unlock();
}
