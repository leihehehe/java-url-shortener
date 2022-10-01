package com.leih.url.account.service;

import com.leih.url.common.enums.SendCodeEnum;
import com.leih.url.common.util.JsonData;
import org.springframework.stereotype.Service;

public interface NotificationService {
  JsonData sendCode(SendCodeEnum sendCodeEnum, String to);
  boolean checkCode(SendCodeEnum sendCodeEnum, String to, String code);
}
