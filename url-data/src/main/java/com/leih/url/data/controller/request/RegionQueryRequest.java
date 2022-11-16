package com.leih.url.data.controller.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class RegionQueryRequest {
    private String code;
    private String startTime;
    private String endTime;
}
