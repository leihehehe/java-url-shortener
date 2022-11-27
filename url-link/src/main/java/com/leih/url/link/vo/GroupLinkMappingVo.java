package com.leih.url.link.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class GroupLinkMappingVo {
    @JsonSerialize(using = ToStringSerializer.class)
    private long id;
    @JsonSerialize(using = ToStringSerializer.class)
    private long groupId;
    private String name;
    private String originalUrl;
    private String domain;
    private String code;
    private String sign;
    private long accountNo;
    private Timestamp expired;
    private Timestamp gmtCreate;
    private Timestamp gmtModified;
    //0 -> default, 1 -> deleted
    private int del;
    private String state;
    private String linkType;
}
