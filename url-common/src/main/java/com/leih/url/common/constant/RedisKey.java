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
  /**
   *
   */
  public static final String PLAN_DELETE_EXPIRED_SCHEDULER_KEY="plan:scheduler:delete:paid_expired";
  /**
   * order:callback:{orderNo}
   */
  public static final String ORDER_CALLBACK_KEY="order:callback:%s";

  /**
   * Available plans per day
   * plan:total_available_times:{accountNo}
   */
  public static final String PLAN_TOTAL_AVAILABLE_TIMES="plan:total_available_times:%s";

  /***
   * Short link code
   * short_link:code:{short link code}
   */
  public static final String SHORT_LINK_CODE_KEY="short_link:code:%s";
}
