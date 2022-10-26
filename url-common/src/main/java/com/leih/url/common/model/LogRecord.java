package com.leih.url.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LogRecord {
    private String ip;
    private Long timestamp;
    private String event;
    /**
     * unique id
     */
    private String uid;
    /**
     * business id
     */
    private String bizId;
    /**
     * log content
     */
    private Object content;
}
