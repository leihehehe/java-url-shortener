package com.leih.url.link.controller.request;

import lombok.Data;

@Data
public class ShortLinkUpdateRequest {
    private Long groupId;
    private Long mappingId;
    private String code;
    private String name;
    private Long domainId;
    private String domainType;
}
