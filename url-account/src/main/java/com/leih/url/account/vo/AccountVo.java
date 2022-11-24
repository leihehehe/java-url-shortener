package com.leih.url.account.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
public class AccountVo {
  private String avatar;

  private String phone;

  private String username;

  private String email;

  @JsonProperty("create_time")
  @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss",timezone = "Australia/Sydney")
  private Timestamp gmtCreate;
}
