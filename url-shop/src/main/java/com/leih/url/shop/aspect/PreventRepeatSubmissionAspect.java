package com.leih.url.shop.aspect;

import com.leih.url.common.constant.RedisKey;
import com.leih.url.common.enums.BizCodeEnum;
import com.leih.url.common.exception.BizException;
import com.leih.url.common.intercepter.LoginInterceptor;
import com.leih.url.common.util.CommonUtil;
import com.leih.url.shop.annotation.PreventRepeatSubmission;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
@Slf4j
public class PreventRepeatSubmissionAspect {
  @Autowired private StringRedisTemplate redisTemplate;
  @Autowired
  private RedissonClient redissonClient;
  @Pointcut("@annotation(preventRepeatSubmission)")
  public void pointCutNoRepeatSubmission(PreventRepeatSubmission preventRepeatSubmission) {}

  @Around("pointCutNoRepeatSubmission(preventRepeatSubmission)")
  public Object around(
      ProceedingJoinPoint joinPoint, PreventRepeatSubmission preventRepeatSubmission)
      throws Throwable {
    HttpServletRequest request =
        ((ServletRequestAttributes)
                Objects.requireNonNull(RequestContextHolder.getRequestAttributes()))
            .getRequest();
    Long accountNo = LoginInterceptor.threadLocal.get().getAccountNo();
    boolean res;
    String type = preventRepeatSubmission.limitType().name();
    if (type.equalsIgnoreCase(PreventRepeatSubmission.Type.PARAM.name())) {
      long lockTime = preventRepeatSubmission.lockTime();
      String ipAddress = CommonUtil.getIpAddress(request);
      MethodSignature signature = (MethodSignature) joinPoint.getSignature();
      Method method = signature.getMethod();
      String methodName = method.getName();
      String className = method.getDeclaringClass().getName();
      String key = "repeat_submission_"+CommonUtil.MD5(String.format("%s-%s-%s-%s", ipAddress, className, methodName, accountNo));
//      res =
//          Boolean.TRUE.equals(
//              redisTemplate.opsForValue().setIfAbsent(key, "1", lockTime, TimeUnit.SECONDS));
      RLock lock = redissonClient.getLock(key);
      res= lock.tryLock(0,lockTime, TimeUnit.SECONDS);
    } else {
      String requestToken = request.getHeader("request-token");
      if (StringUtils.hasLength(requestToken)) {
        String key = String.format(RedisKey.SUBMIT_ORDER_TOKEN_KEY, accountNo, requestToken);
        res = Boolean.TRUE.equals(redisTemplate.delete(key));
      } else {
        throw new BizException(BizCodeEnum.ORDER_CREATE_TOKEN_EQUAL_FAIL);
      }
    }
    if (!res) {
      log.error("Order has been submitted repeatedly");
      return null;
    }
    // before
    Object proceed = joinPoint.proceed();
    // after
    return proceed;
  }
}
