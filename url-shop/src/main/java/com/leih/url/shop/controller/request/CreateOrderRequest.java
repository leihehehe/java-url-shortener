package com.leih.url.shop.controller.request;

import lombok.Data;

@Data
public class CreateOrderRequest {
    private Long productId;
    private Integer buyNum;
    private Double totalPrice;
    private Double payPrice;
    private String paymentType;
    private String billType;
    private String billHeader;
    private String billContent;
    private String billReceiverPhone;
    private String billReceiverEmail;
    private String token;
    private String clientType;
}
