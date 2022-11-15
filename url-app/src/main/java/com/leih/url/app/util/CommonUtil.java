package com.leih.url.app.util;
import com.leih.url.app.model.DeviceInfo;
import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

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

  public static String getOSVersion(String agent){
    String osVersion="";
    if(StringUtils.isNotBlank(agent)){
      String[] splitArr = agent.substring(agent.indexOf("(") + 1, agent.indexOf(")")).split(";");
      if(splitArr.length>0){
        osVersion=splitArr[1];
      }
    }
    return osVersion;
  }

  public static DeviceInfo getDeviceInfo(String agent){
    UserAgent userAgent = UserAgent.parseUserAgentString(agent);
    OperatingSystem os = userAgent.getOperatingSystem();
    Browser browser = userAgent.getBrowser();
    DeviceInfo deviceInfo = DeviceInfo.builder().browserName(browser.getGroup().getName())
            .deviceType(os.getDeviceType().getName())
            .deviceManufacturer(browser.getManufacturer().getName())
            .osVersion(getOSVersion(agent))
            .os(os.getGroup().getName())
            .build();
    return deviceInfo;
  }
}
