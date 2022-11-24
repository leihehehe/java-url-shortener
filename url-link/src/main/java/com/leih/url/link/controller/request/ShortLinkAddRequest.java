package com.leih.url.link.controller.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.sql.Timestamp;
import java.util.Date;

@Data
public class ShortLinkAddRequest {
    private Long groupId;
    private String name;
    private String originalUrl;
    private Long domainId;
    private String domainType;
    @JsonFormat(timezone = "Australia/Sydney",pattern = "dd-MM-yyyy HH:mm:ss")
    private Timestamp expired;
}
