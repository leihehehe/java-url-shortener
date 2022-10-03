package com.leih.url.account.controller;

import com.google.code.kaptcha.Producer;
import com.leih.url.account.controller.request.SendCodeRequest;
import com.leih.url.account.service.NotificationService;
import com.leih.url.common.constant.RedisKey;
import com.leih.url.common.enums.BizCodeEnum;
import com.leih.url.common.enums.SendCodeEnum;
import com.leih.url.common.util.CommonUtil;
import com.leih.url.common.util.JsonData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
@RequestMapping("/api/notification/v1")
public class NotificationController {
  /** Expired time */
  private static final long CAPTCHA_CODE_EXPIRED = 1000 * 10 * 60;

  @Autowired Producer captchaProducer;
  @Autowired StringRedisTemplate redisTemplate;
  @Autowired NotificationService notificationService;

  /**
   * Generate captcha image
   *
   * @param request
   * @param response
   */
  @GetMapping("captcha")
  public void getCaptcha(HttpServletRequest request, HttpServletResponse response) {
    String captchaText = captchaProducer.createText();
    log.info("Getting captcha code:{}", captchaText);

    // store captcha info into redis
    redisTemplate
        .opsForValue()
        .set(getCaptchaRedisKey(request), captchaText, CAPTCHA_CODE_EXPIRED, TimeUnit.MILLISECONDS);

    BufferedImage image = captchaProducer.createImage(captchaText);
    try (ServletOutputStream outputStream = response.getOutputStream()) {
      ImageIO.write(image, "png", outputStream);
      outputStream.flush();
    } catch (IOException e) {
      log.error("Failed to get output stream: {}", e.getMessage());
    }
  }

  /**
   * Get redis key for captcha
   *
   * @param request
   * @return
   */
  private String getCaptchaRedisKey(HttpServletRequest request) {
    String ip = CommonUtil.getIpAddress(request);
    String userAgent = request.getHeader("User-Agent");
    String key = RedisKey.CAPTCHA_KEY + CommonUtil.MD5(ip + userAgent);
    log.info("Getting captcha key in redis: {}", key);
    return key;
  }

  /***
   * Send SMS code
   * @param sendCodeRequest
   * @param request
   * @return
   */
  @PostMapping("send_code")
  public JsonData sendSMS(
      @RequestBody SendCodeRequest sendCodeRequest, HttpServletRequest request) {
    // Check captcha first
    String captchaKeyInRedis = getCaptchaRedisKey(request);
    String captchaValueInRedis = redisTemplate.opsForValue().get(captchaKeyInRedis);
    String captchaFromClient = sendCodeRequest.getCaptcha();
    if (captchaValueInRedis != null && captchaValueInRedis.equals(captchaFromClient)) {
      // success
      redisTemplate.delete(captchaKeyInRedis);
      // send sms
      return notificationService.sendCode(SendCodeEnum.USER_REGISTER, sendCodeRequest.getTo());
    }
    return JsonData.buildResult(BizCodeEnum.CODE_CAPTCHA_ERROR);
  }
}
