package com.leih.url.link.vo;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
public class DomainVo {
  private long id;

  private String domainType;

  private String value;

  private int delete;

  private long accountNo;

  private Timestamp gmtCreate;

  private Timestamp gmtModified;
}
