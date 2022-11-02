package com.leih.app.util;
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
