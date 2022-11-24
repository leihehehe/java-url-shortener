package com.leih.url.common.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheckUtil {
  private static final Pattern MAIL_PATTERN =
      Pattern.compile(
          "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9AZ]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$");
  private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+(?:[0-9] ?){6,14}[0-9]$");

  public static boolean isEmail(String email) {
    if (email == null || "".equals(email)) {
      return false;
    }
    Matcher m = MAIL_PATTERN.matcher(email);
    return m.matches();
  }

  public static boolean isPhone(String phone) {
    if (phone == null || "".equals(phone)) {
      return false;
    }
    Matcher m = PHONE_PATTERN.matcher(phone);
    return m.matches();
  }

  public static boolean isUsername(String username) {
    if (username == null || "".equals(username)) {
      return false;
    }
    if (username.contains("@") || username.contains("+")) {
      return false;
    }
    return true;
    }
}
