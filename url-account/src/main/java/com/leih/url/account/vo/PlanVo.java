package com.leih.url.account.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

@Data
public class PlanVo {
  private Long id;
  private Integer dayLimit;
  private Integer dayUsed;
  private Integer totalLimit;
  private Long accountNo;
  private String orderNo;
  private String level;
  private Timestamp expiredDate;
  private String pluginType;
  private Long productId;
}
