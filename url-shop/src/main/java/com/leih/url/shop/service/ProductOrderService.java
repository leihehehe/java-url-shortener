package com.leih.url.shop.service;

import com.leih.url.common.enums.PaymentTypeEnum;
import com.leih.url.common.model.EventMessage;
import com.leih.url.common.util.JsonData;
import com.leih.url.shop.controller.request.CreateOrderRequest;
import com.leih.url.shop.controller.request.ProductOrderPageRequest;

import java.util.Map;

public interface ProductOrderService {
    Map<String, Object> paginateProductOrder(ProductOrderPageRequest request);

    String queryProductOrderState(String orderNo);

    JsonData createOrder(CreateOrderRequest createOrderRequest);

    boolean cancelProductOrder(EventMessage eventMessage);
    JsonData payOrder(String orderNo);
    JsonData processOrderCallbackMsg(PaymentTypeEnum paymentType, Map<String,String> paramMap);

    /**
     * Handle order messages
     * @param eventMessage
     */
    void handleProductOrderMsg(EventMessage eventMessage);

}
