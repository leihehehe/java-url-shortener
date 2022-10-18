package com.leih.url.shop.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentInfoVo {
    /**
     * Order Number
     */
    private String orderNo;
    /**
     * Total price of the payment
     */
    private Double payPrice;
    /**
     * Payment method
     */
    private String paymentType;
    /**
     * Client type
     */
    private String clientType;
    /**
     * Product name
     */
    private String productName;
    /**
     * Description
     */
    private String description;
    /**
     * Timeout milliseconds
     */
    private Long orderPaymentTimeoutMills;
    /**
     * Account number
     */
    private Long accountNo;
}
