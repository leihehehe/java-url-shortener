package com.leih.url.shop.vo;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

@Data
public class ProductOrderVo {
  private Long id;
  private Long productId;
  private String productName;
  private Double productPrice;
  private String productSnapshot;
  private Integer buyNum;
  private String orderNo;
  private String state;
  private Date createTime;
  private Double totalPrice;
  private Double payPrice;
  private String paymentType;
  private String nickname;
  private Long accountNo;
  private Integer del;
  private Timestamp gmtCreate;
  private Timestamp gmtModified;
  private String billType;
  private String billHeader;
  private String billContent;
  private String billReceiverPhone;
  private String billReceiverEmail;
}
