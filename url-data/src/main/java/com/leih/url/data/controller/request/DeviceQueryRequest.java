package com.leih.url.data.controller.request;

import lombok.Data;

@Data
public class DeviceQueryRequest {
    private String code;
    private String startTime;
    private String endTime;
}
