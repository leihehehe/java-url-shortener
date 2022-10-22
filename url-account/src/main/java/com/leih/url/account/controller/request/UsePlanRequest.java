package com.leih.url.account.controller.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class UsePlanRequest {
    private Long accountNo;
    private String bizId;
}
