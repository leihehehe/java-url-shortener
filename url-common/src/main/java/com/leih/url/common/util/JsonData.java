package com.leih.url.common.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leih.url.common.enums.BizCodeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.IOException;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JsonData {
  static ObjectMapper objectMapper = new ObjectMapper();
  /** Status code: 0 -> Success */
  private Integer code;
  /** Data */
  private Object data;
  /** Message */
  private String msg;

  /**
   * Success message without data
   *
   * @return
   */
  public static JsonData buildSuccess() {
    return new JsonData(0, null, null);
  }

  /**
   * Success message with data
   *
   * @param data
   * @return
   */
  public static JsonData buildSuccess(Object data) {
    return new JsonData(0, data, null);
  }

  /**
   * 􀥦􁨳􀒅􀖃􀙁􀵈􁬿􀗞􀯳
   *
   * @param msg
   * @return
   */
  public static JsonData buildError(String msg) {
    return new JsonData(-1, null, msg);
  }

  /**
   * Error message with description
   *
   * @param code
   * @param msg
   * @return
   */
  public static JsonData buildCodeAndMsg(int code, String msg) {
    return new JsonData(code, null, msg);
  }

  public static JsonData buildResult(BizCodeEnum bizCodeEnum) {
    return JsonData.buildCodeAndMsg(bizCodeEnum.getCode(), bizCodeEnum.getMessage());
  }

  /**
   * Get Remote data
   *
   * @param typeReference
   * @return
   * @param <T>
   * @throws IOException
   */
  public <T> T getData(TypeReference<T> typeReference) throws IOException {
    return objectMapper.readValue(JsonUtil.obj2Json(data), typeReference);
  }
}
