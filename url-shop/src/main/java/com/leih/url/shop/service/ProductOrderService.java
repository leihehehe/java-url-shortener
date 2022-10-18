package com.leih.url.shop.service;

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

}
