package com.leih.url.common.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.text.SimpleDateFormat;
import java.util.List;

public class JsonUtil {
  private static final ObjectMapper mapper = new ObjectMapper();

  /** Configuration for Jackson */
  static {
    mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
    // Always serialize all the properties in the object
    mapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
    // Do not throw exception when there are unkown properties in the object
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    // Do not throw exception when it is an empty object
    mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    // Do not write dates as timestamps
    mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    mapper.setDateFormat(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss"));
  }

  /**
   * Convert object to JSON String
   *
   * @param obj
   * @return
   */
  public static String obj2Json(Object obj) {
    String jsonStr = null;
    try {
      jsonStr = mapper.writeValueAsString(obj);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return jsonStr;
  }

  /**
   * Convert Json String to Object
   *
   * @param jsonStr
   * @param beanType
   * @return
   * @param <T>
   */
  public static <T> T json2Obj(String jsonStr, Class<T> beanType) {
    T obj = null;
    try {
      obj = mapper.readValue(jsonStr, beanType);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return obj;
  }

  /***
   * Json String to list of objects
   * @param jsonData
   * @param beanType
   * @return
   * @param <T>
   */
  public static <T> List<T> json2List(String jsonData, Class<T> beanType) {
    JavaType javaType = mapper.getTypeFactory().constructParametricType(List.class, beanType);
    try {
      List<T> list = mapper.readValue(jsonData, javaType);
      return list;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Convert Object to Bytes
   *
   * @param obj
   * @return
   */
  public static byte[] obj2Bytes(Object obj) {
    byte[] byteArr = null;
    try {
      byteArr = mapper.writeValueAsBytes(obj);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return byteArr;
  }

  /**
   * Convert bytes to object
   *
   * @param byteArr
   * @param beanType
   * @return
   * @param <T>
   */
  public static <T> T bytes2Obj(byte[] byteArr, Class<T> beanType) {
    T obj = null;
    try {
      obj = mapper.readValue(byteArr, beanType);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return obj;
  }
}
