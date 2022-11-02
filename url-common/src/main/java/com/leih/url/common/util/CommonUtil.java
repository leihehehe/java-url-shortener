package com.leih.url.common.util;

import com.google.common.hash.Hashing;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.util.*;

@Slf4j
public class CommonUtil {
  private static final String ALL_CHAR_NUM =
      "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

  /**
   * Get IP Address
   *
   * @param request
   * @return
   */
  public static String getIpAddress(HttpServletRequest request) {
    String ipAddress = null;
    try {
      ipAddress = request.getHeader("x-forwarded-for");
      if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
        ipAddress = request.getHeader("Proxy-Client-IP");
      }
      if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
        ipAddress = request.getHeader("WLProxy-Client-IP");
      }
      if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
        ipAddress = request.getRemoteAddr();
        if (ipAddress.equals("127.0.0.1")) {
          try {
            InetAddress inet = null;
            // get local ip address
            inet = InetAddress.getLocalHost();
            ipAddress = inet.getHostAddress();
          } catch (UnknownHostException e) {
            e.printStackTrace();
          }
        }
      }
      if (ipAddress != null && ipAddress.length() > 15) {
        // "***.***.***.***".length()
        if (ipAddress.indexOf(",") > 0) {
          ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
        }
      }
    } catch (Exception e) {
      ipAddress = "";
    }
    return ipAddress;
  }

  /**
   * Get all request headers
   *
   * @param request
   * @return
   */
  public static Map<String, String> getAllRequestHeader(HttpServletRequest request) {
    Enumeration<String> headerNames = request.getHeaderNames();
    Map<String, String> map = new HashMap<>();
    while (headerNames.hasMoreElements()) {
      String key = headerNames.nextElement();
      String value = request.getHeader(key);
      map.put(key, value);
    }
    return map;
  }

  /**
   * MD5 encode
   *
   * @param data
   * @return
   */
  public static String MD5(String data) {
    try {
      MessageDigest md = MessageDigest.getInstance("MD5");
      byte[] array = md.digest(data.getBytes("UTF-8"));
      StringBuilder sb = new StringBuilder();
      for (byte item : array) {
        sb.append(Integer.toHexString((item & 0xFF) | 0x100).substring(1, 3));
      }
      return sb.toString().toUpperCase();
    } catch (Exception exception) {
    }
    return null;
  }

  /***
   * Get Random code
   * @param length
   * @return
   */
  public static String getRandomCode(int length) {
    String sources = "0123456789";
    Random random = new Random();
    StringBuilder sb = new StringBuilder();
    for (int j = 0; j < length; j++) {
      sb.append(sources.charAt(random.nextInt(9)));
    }
    return sb.toString();
  }

  /**
   * Get current system timestamp
   *
   * @return
   */
  public static long getCurrentTimestamp() {
    return System.currentTimeMillis();
  }

  /**
   * Generate uuid
   *
   * @return
   */
  public static String generateUUID() {
    return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 32);
  }

  /**
   * Get a string with the random length
   *
   * @param length
   * @return
   */
  public static String getStringNumRandom(int length) {
    Random random = new Random();
    StringBuilder saltString = new StringBuilder(length);
    for (int i = 1; i <= length; ++i) {
      saltString.append(ALL_CHAR_NUM.charAt(random.nextInt(ALL_CHAR_NUM.length())));
    }
    return saltString.toString();
  }

  /**
   * Send json message to frontend
   *
   * @param response
   * @param obj
   */
  public static void sendJsonMessage(HttpServletResponse response, Object obj) {
    response.setContentType("application/json;charset=utf-8");
    try (PrintWriter writer = response.getWriter()) {
      writer.print(JsonUtil.obj2Json(obj));
      response.flushBuffer();
    } catch (IOException e) {
      log.warn("Send exception json data to frontend " + e);
    }
  }
  public static void sendHtmlMessage(HttpServletResponse response, JsonData jsonData) {
    response.setContentType("text/html;charset=utf-8");
    try (PrintWriter writer = response.getWriter()) {
      writer.write(jsonData.getData().toString());
      writer.flush();
    } catch (IOException e) {
      log.warn("Send exception json data to frontend " + e);
    }
  }
  /**
   * Murmur hash
   * @param param
   * @return
   */
  public static long MurmurHash(String param){
    return Hashing.murmur3_32().hashUnencodedChars(param).padToLong();
  }

  /**
   * Add prefix for url
   * @return
   */
  public static String addUrlPrefix(String url){
    return IdUtil.generateSnowFlakeId()+"|"+url;
  }

  /**
   * Remove prefix for url
   * @param url
   * @return
   */
  public static String removeUrlPrefix(String url){
    return url.substring(url.indexOf("|")+1);
  }

  /**
   * Used to resolve the url conflict
   * @param url
   * @return
   */
  public static String addUrlPrefixVersion(String url){
    String version = url.substring(0, url.indexOf("|"));
    String originalUrl = url.substring(url.indexOf("|")+1);
    Long newVersion = Long.parseLong(version)+1;
    String newUrl = newVersion+"|"+originalUrl;
    return newUrl;
  }

  /**
   * Generate unique Device ID
   * @param map
   * @return
   */
  public static String generateUniqueDeviceId(Map<String,String> map){
    String deviceId = MD5(map.toString());
    return deviceId;
  }
}
