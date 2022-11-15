package com.leih.url.data.controller.request;

import lombok.Data;

@Data
public class VisitRecordPageRequest {
    private String code;
    private int size;
    private int page;
}
