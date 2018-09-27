package io.github.ionething.springboot.redis.locks;

import java.util.concurrent.TimeUnit;

/**
 *
 * interface of distributed re-entering lock
 * @author Vincent
 *
 */
public interface DistReentrantLock {

    /**
     * blocking acquire lock
     * @throws Exception
     */
    void lock() throws Exception;

    /**
     * try to acquire lock once
     * @return
     * @throws Exception
     */
    boolean tryLock() throws Exception;

    /**
     * try to acquire lock until timeout
     * @param timeout
     * @param unit
     * @return
     * @throws Exception
     */
    boolean tryLock(long timeout, TimeUnit unit) throws Exception;

    /**
     * release lock
     */
    void unlock();
}
