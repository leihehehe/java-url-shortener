package com.leih.url.link.controller.request;

import lombok.Data;

import java.util.Date;

@Data
public class ShortLinkAddRequest {
    private Long groupId;
    private String name;
    private String originalUrl;
    private Long domainId;
    private String domainType;
    private Date expired;
}