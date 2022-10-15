package com.leih.url.link.vo;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class GroupLinkMappingVo {
    private long id;
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
