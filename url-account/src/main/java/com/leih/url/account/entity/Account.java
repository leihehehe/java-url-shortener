package com.leih.url.account.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.math.BigInteger;
import java.sql.Timestamp;

@Data
@Entity
@Table(
    name = "account",
    uniqueConstraints = {
      @UniqueConstraint(name = "uk_phone", columnNames = "phone"),
      @UniqueConstraint(name = "uk_account", columnNames = "account_no")
    })
public class Account {
  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(name = "account_no")
  private long accountNo;

  @Column(name = "avatar")
  private String avatar;

  @Column(name = "phone", length = 128)
  private String phone;

  @Column(name = "username")
  private String username;

  @Column(name = "password", length = 128)
  private String password;

  @Column(name = "secret", length = 64)
  private String secret;

  @Column(name = "email", length = 128)
  private String email;

  @Column(name = "auth",length = 32)
  private String auth;

  @Column(name = "gmt_create",insertable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
  private Timestamp gmtCreate;

  @Column(
          name = "gmt_modified", insertable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
  private Timestamp gmtModified;
}
