# redis-spring-boot-starter
redis-spring-boot-starter

This is a simple spring boot starter to use Jedis. 

## Features
- [x] Auto configuration.
- [x] JedisPool and RedisClient.
- [x] Distributed re-entering lock - RedisReentrantLock.
- [x] Distributed limiting.

## Contact
Github issues

Mail: vincent7xin@gmail.com

## Quick Start

compile and install:

```
mvn clean install
```

maven dependency:

```xml
<dependency>
    <groupId>io.github.ionething</groupId>
    <artifactId>redis-spring-boot-starter</artifactId>
    <version>1.0.1</version>
</dependency>
```

### RedisClient and JedisPool

```java
    @Autowired
    private RedisClient redisClient;

    @Autowired
    private JedisPool jedisPool;
    
    // Or from redisClient
    // private JedisPool jedisPool = redisClient.getJedisPool()

```

### RedisReentrantLock

```java
    // Or spring ioc
    RedisReentrantLock lock = new RedisReentrantLock(redisClient.getJedisPool(), "name");
    
    try {
        lock.lock();
        
        // doing something
        
    } finally {
        lock.unlock();
    }

```

### Limiting

doing


## Spring Boot Properties
application.properties

```
redis.host: 127.0.0.1
redis.port: 6379
redis.password: 
redis.ssl: false
redis.pool.maxIdle: 8
redis.pool.minIdle: 0
redis.pool.maxActive: 8
redis.pool.maxWait: -1ms
```
 
## TODO
- support redis cluster.

## custom spring boot starter
[spring boot docs](https://docs.spring.io/spring-boot/docs/2.0.5.RELEASE/reference/htmlsingle/#boot-features-custom-starter)