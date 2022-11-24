package com.leih.url.account.service.impl;

import com.leih.url.account.component.SmsComponent;
import com.leih.url.account.service.AccountService;
import com.leih.url.account.service.NotificationService;
import com.leih.url.common.constant.RedisKey;
import com.leih.url.common.enums.BizCodeEnum;
import com.leih.url.common.enums.SendCodeEnum;
import com.leih.url.common.util.CheckUtil;
import com.leih.url.common.util.CommonUtil;
import com.leih.url.common.util.JsonData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class NotificationServiceImpl implements NotificationService {
  /** 10 minutes */
  private static final int CODE_EXPIRED = 60 * 1000 * 10;

  @Autowired private SmsComponent smsComponent;
  @Autowired private StringRedisTemplate redisTemplate;

  /**
   * Send code(email/sms)
   *
   * @param sendCodeEnum
   * @param to
   * @return
   */
  public JsonData sendCode(SendCodeEnum sendCodeEnum, String to) {
    if (!CheckUtil.isPhone(to)) {
      return JsonData.buildResult(BizCodeEnum.CODE_TO_ERROR);
    }
    String codeKeyInRedis = String.format(RedisKey.CHECK_CODE_KEY, sendCodeEnum.name(), to);
    String codeValueInRedis = redisTemplate.opsForValue().get(codeKeyInRedis);
    if (StringUtils.hasLength(codeValueInRedis)) {
      // if there's existing code in the redis
      long ttl = Long.parseLong(codeValueInRedis.split("_")[1]);
      // check if current timestamp - ttl > 60s, if yes, send code, otherwise do not send code.
      long intervalTime = CommonUtil.getCurrentTimestamp() - ttl;
      if (intervalTime < (1000 * 60)) {
        log.info("Failed to send code within 60 s -> time interval: {}", intervalTime);
        return JsonData.buildResult(BizCodeEnum.CODE_LIMITED);
      }
    }
    // new code generated
    String code = CommonUtil.getRandomCode(6);
    String value = code + "_" + CommonUtil.getCurrentTimestamp();
    redisTemplate.opsForValue().set(codeKeyInRedis, value, CODE_EXPIRED, TimeUnit.MILLISECONDS);
    boolean result;
    // TODO: sending emails
    result = smsComponent.sendSms(to, "[Easy URL Shortener] Your code is " + code + "(valid for 10 minutes)");
    return result
        ? JsonData.buildResult(BizCodeEnum.CODE_SUCCESS)
        : JsonData.buildResult(BizCodeEnum.CODE_FAILED);
  }

  @Override
  public boolean checkCode(SendCodeEnum sendCodeEnum, String to, String code) {
    String codeKeyInRedis = String.format(RedisKey.CHECK_CODE_KEY, sendCodeEnum.name(), to);
    String codeValueInRedis = redisTemplate.opsForValue().get(codeKeyInRedis);
    if(StringUtils.hasLength(codeValueInRedis)){
      //if code exists in the redis, the code hasn't expired
      String codeValue = codeValueInRedis.split("_")[0];
      if(codeValue.equalsIgnoreCase(code)){
        log.info("expected: {}, given: {}",codeValue,code);
        redisTemplate.delete(codeKeyInRedis);
        return true;
      }
    }
    return false;
  }
}
