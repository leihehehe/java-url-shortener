package com.leih.url.account.entity;

import lombok.Data;

import javax.naming.Name;
import javax.persistence.*;

@Data
@Entity
@Table(
        name = "plan",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_order_no", columnNames ={"order_no","account_no"}),
        },
        indexes = {@Index(name = "idx_account_no",columnList = "account_no")}
)
public class Plan {
  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;
  @Column(name = "day_limit")
  private int dayLimit;
  @Column(name="day_used")
  private int dayUsed;
  @Column(name = "account_no")
  private long accountNo;
  @Column(name = "order_no",length = 64)
  private String orderNo;
  @Column(name = "level",length = 64)
  private String level;
  @Column(name = "expired_date")
  private java.sql.Date expiredDate;
  @Column(name = "plugin_type",length = 64)
  private String pluginType;
  @Column(name="product_id")
  private long productId;
  @Column(name = "gmt_create", columnDefinition = "datetime DEFAULT CURRENT_TIMESTAMP")
  private java.sql.Timestamp gmtCreate;
  @Column(
          name = "gmt_modified",
          columnDefinition = "datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
  private java.sql.Timestamp gmtModified;

}
