package com.leih.url.account.aspect;

import com.leih.url.account.annotation.CacheLock;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Aspect
@Slf4j
@Component
public class CacheLockAspect {
  private static final String LOCK_VALUE = "locked";
  @Autowired private RedissonClient redissonClient;

  @Pointcut("@annotation(cacheLock)")
  public void pointCutCacheLock(CacheLock cacheLock) {}

  @Around("pointCutCacheLock(cacheLock)")
  public void cacheLockPoint(ProceedingJoinPoint joinPoint, CacheLock cacheLock) {
    String key = cacheLock.lockedPrefix();
    RLock lock = redissonClient.getLock(key);
    boolean res;
    try {
      //redisson will help us to extend the expiration time if the thread is still running
      res = lock.tryLock(cacheLock.expireTime(), TimeUnit.SECONDS);
      if (!res) {
        log.error("Failed to get lock, the key has been locked by other nodes");
      } else {
        log.info("Got lock:{}", key);
        joinPoint.proceed();
        lock.unlock();
        log.info("Lock has been released");
      }
    } catch (Throwable e) {
      log.error("Exception thrown - failed to get lock: {}", e.getMessage());
    }
  }
}
