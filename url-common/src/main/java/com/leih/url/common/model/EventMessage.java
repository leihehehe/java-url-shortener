package com.leih.url.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventMessage implements Serializable {
    /**
     * Message ID
     */
    private String messageId;
    /**
     * Event Type
     */
    private String eventMessageType;
    /**
     * Business ID
     */
    private String bizId;
    /**
     * Account Number
     */
    private Long accountNo;
    /**
     * Message Body
     */
    private String content;
    /**
     * Comments
     */
    private String comment;
}

