package com.leih.url.shop.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "product_order",uniqueConstraints = {
        @UniqueConstraint(name = "uk_query",columnNames = {"order_no","account_no"})
})
public class ProductOrder {
  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(name = "product_id")
  private Long productId;
  @Column(name = "product_name",length = 64)
  private String productName;
  @Column(name = "product_price")
  private Double productPrice;
  @Column(name = "product_snapshot",length = 2048)
  private String productSnapshot;
  @Column(name = "buy_num")
  private Integer buyNum;
  @Column(name = "order_no",length = 64)
  private String orderNo;
  @Column(name = "state",length = 11)
  private String state;
  @Column(name = "create_time")
  private Timestamp createTime;
  @Column(name = "total_price")
  private Double totalPrice;
  @Column(name = "pay_price")
  private Double payPrice;
  @Column(name = "payment_type",length = 64)
  private String paymentType;
  @Column(name = "nickname",length = 64)
  private String nickname;
  @Column(name = "account_no")
  private Long accountNo;
  @Column(name = "del")
  private Integer del;
  @Column(
          name = "gmt_create",
          insertable = false,
          updatable = false,
          columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
  private Timestamp gmtCreate;

  @Column(
          name = "gmt_modified",
          insertable = false,
          updatable = false,
          columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
  private Timestamp gmtModified;
  @Column(name = "bill_type",length = 32)
  private String billType;
  @Column(name = "bill_header",length = 200)
  private String billHeader;
  @Column(name = "bill_content",length = 200)
  private String billContent;
  @Column(name = "bill_receiver_phone",length = 32)
  private String billReceiverPhone;
  @Column(name = "bill_receiver_email",length = 200)
  private String billReceiverEmail;
}
