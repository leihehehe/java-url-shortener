package com.leih.url.account.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@Table(
    name = "plan_task",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "uk_msg_id",
          columnNames = {"message_id"}),
    },
    indexes = {
      @Index(name = "idx_release", columnList = "account_no"),
      @Index(name = "idx_release", columnList = "id")
    })
@Entity
public class PlanTask {
  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(name = "account_no")
  private long accountNo;

  @Column(name = "plan_id")
  private long planId;

  @Column(name = "use_times")
  private int useTimes;

  @Column(name = "lock_state", length = 16)
  private String lockState;

  @Column(name = "message_id", length = 64)
  private String messageId;

  @Column(name = "gmt_create",insertable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
  private Timestamp gmtCreate;

  @Column(
          name = "gmt_modified", insertable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
  private Timestamp gmtModified;
}
