package com.leih.url.link.controller.request;

import lombok.Data;

@Data
public class ShortLinkDeleteRequest {
    private Long groupId;
    private Long mappingId;
    private String code;
}
