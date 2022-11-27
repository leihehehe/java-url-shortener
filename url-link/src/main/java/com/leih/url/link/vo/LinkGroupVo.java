package com.leih.url.link.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.sql.Timestamp;

/***
 * Value object -> LinkGroup
 */
@Data
public class LinkGroupVo {
    @JsonSerialize(using = ToStringSerializer.class)
    private long id;
    private String name;
    private long accountNo;
    private Timestamp gmtCreate;
    private Timestamp gmtModified;
}
