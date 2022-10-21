package com.leih.url.account.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(
        name = "plan",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_order_no", columnNames ={"order_no","account_no"}),
        },
        indexes = {@Index(name = "idx_account_no",columnList = "account_no")}
)
public class  Plan {
  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(name = "day_limit")
  private Integer dayLimit;
  @Column(name="day_used")
  private Integer dayUsed;
  @Column(name="total_limit")
  private Integer totalLimit;
  @Column(name = "account_no")
  private Long accountNo;
  @Column(name = "order_no",length = 64)
  private String orderNo;
  @Column(name = "level",length = 64)
  private String level;
  @Column(name = "expired_date")
  private Timestamp expiredDate;
  @Column(name = "plugin_type",length = 64)
  private String pluginType;
  @Column(name="product_id")
  private Long productId;
  @Column(name = "gmt_create",insertable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
  private Timestamp gmtCreate;
  @Column(
          name = "gmt_modified", insertable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
  private Timestamp gmtModified;

}
