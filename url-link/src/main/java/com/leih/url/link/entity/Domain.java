package com.leih.url.link.entity;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@Entity
@Table(name = "domain")
public class Domain {
  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "domain_type", length = 11)
  private String domainType;

  @Column(name = "value")
  private String value;

  @Column(name = "del")
  private int del;

  @Column(name = "account_no")
  private Long accountNo;

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
}
