package com.leih.url.link.controller.request;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class ShortLinkPageRequest {
    private Long groupId;
    private int page;
    private int size;
}
