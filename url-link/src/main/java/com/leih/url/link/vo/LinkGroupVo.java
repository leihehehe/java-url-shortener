package com.leih.url.link.vo;

import lombok.Data;

import java.sql.Timestamp;

/***
 * Value object -> LinkGroup
 */
@Data
public class LinkGroupVo {
    private long id;
    private String name;
    private long accountNo;
    private Timestamp gmtCreate;
    private Timestamp gmtModified;
}
