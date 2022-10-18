package com.leih.url.common.constant;

public class RedisKey {
  /**
   * SMS cache code.
   * code:{type}:{email/phone}
   * */
  public static final String CHECK_CODE_KEY = "code:%s:%s";

  public static final String CAPTCHA_KEY = "url-account-service:captcha:";
  /**
   * order:submit:{accountNo}:{token}
   */
  public static final String SUBMIT_ORDER_TOKEN_KEY="order:submit:%s:%s";
}
