package com.leih.url.data.controller.request;

import lombok.Data;

@Data
public class VisitTrendQueryRequest {
    private String code;
    private String type;
    private String startTime;
    private String endTime;
}
