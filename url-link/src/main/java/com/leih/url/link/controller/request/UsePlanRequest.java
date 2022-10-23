package com.leih.url.link.controller.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UsePlanRequest {
    private Long accountNo;
    private String bizId;
}
