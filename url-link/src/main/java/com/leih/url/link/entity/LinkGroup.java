package com.leih.url.link.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@Entity
@Table(name = "link_group")
public class LinkGroup {
  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(name = "name", length = 255)
  private String name;

  @Column(name = "account_no")
  private long accountNo;

  @Column(name = "gmt_create")
  @CreationTimestamp
  private Timestamp gmtCreate;

  @Column(
      name = "gmt_modified")
  @UpdateTimestamp
  private Timestamp gmtModified;
}
