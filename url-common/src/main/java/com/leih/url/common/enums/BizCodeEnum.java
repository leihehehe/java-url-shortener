package com.leih.url.common.enums;

import lombok.Getter;

public enum BizCodeEnum {
  /** Short Url Groups */
  GROUP_REPEAT(23001, "Duplicate group name"),
  GROUP_OPER_FAIL(23503, "Failed operation on group name"),
  GROUP_NOT_EXIST(23404, "The group does not exist"),
  GROUP_CREATE_FAILED(23405, "Failed to create group"),
  GROUP_DELETE_FAILED(23406, "Failed to delete group"),
  GROUP_DETAILS_FAILED(23407, "Failed to get group details"),
  GROUP_LIST_FAILED(23408, "Failed to list all groups"),
  GROUP_UPDATE_FAILED(23407, "Failed to update  group "),

  /** SMS Code */
  CODE_TO_ERROR(240001, "Invalid phone number"),
  CODE_LIMITED(240002, "You are sending code too frequent, please try again later"),
  CODE_ERROR(240003, "Incorrect code"),
  CODE_SUCCESS(240004, "Code has been sent"),
  CODE_FAILED(240005, "Failed to send code"),
  CODE_CAPTCHA_ERROR(240101, "Incorrect CAPTCHA"),
  /** Account */
  ACCOUNT_REPEAT(250001, "The account has been created"),
  ACCOUNT_UNREGISTER(250002, "The account does not exist"),
  ACCOUNT_PWD_ERROR(250003, "Invalid username or password"),
  ACCOUNT_UNLOGIN(250004, "You haven't logged in"),
  /** Short url */
  SHORT_LINK_NOT_EXIST(260404, "The url does not exist"),
  /** Order */
  ORDER_CONFIRM_PRICE_FAIL(280002, "Create Order - Failed to confirm the price"),
  ORDER_CONFIRM_REPEAT(280008, "Duplicate Submission for order"),
  ORDER_CONFIRM_TOKEN_EQUAL_FAIL(280009, "Invalid order token"),
  ORDER_CONFIRM_NOT_EXIST(280010, "The order does not exist"),
  /** Payment */
  PAY_ORDER_FAIL(300001, "Failed to create order"),
  PAY_ORDER_CALLBACK_SIGN_FAIL(300002, "Invalid sign"),
  PAY_ORDER_CALLBACK_NOT_SUCCESS(300003, "Failed to update order"),
  PAY_ORDER_NOT_EXIST(300005, "The order does not exist"),
  PAY_ORDER_STATE_ERROR(300006, "Abnormal order detected"),
  PAY_ORDER_PAY_TIMEOUT(300007, "Time out"),
  /** Flow Control */
  CONTROL_FLOW(500101, "Rate limit"),
  CONTROL_DEGRADE(500201, "Circuit break"),
  CONTROL_AUTH(500301, "Authentication control"),
  /** 􁁞􁰁􀛱􀶙􀖢 */
  TRAFFIC_FREE_NOT_EXIST(600101, "We currently don't offer free data, please contact us."),
  TRAFFIC_REDUCE_FAIL(600102, "Insufficient data, failed to deduct data"),

  TRAFFIC_EXCEPTION(600103, "Abnormal data, user does not have data"),

  /** General Code */
  OPS_REPEAT(110001, "Duplicate operation"),
  OPS_NETWORK_ADDRESS_ERROR(110002, "Invalid network address"),
  /** Files */
  FILE_UPLOAD_USER_IMG_FAIL(700101, "Failed to upload user avatar"),

  /** Sharding jdbc code */
  DB_ROUTE_NOT_FOUND(800101, "Failed to upload user avatar");
  @Getter private String message;
  @Getter private int code;

  BizCodeEnum(int code, String message) {
    this.code = code;
    this.message = message;
  }
}
