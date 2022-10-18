package com.leih.url.shop.manager;

import com.leih.url.shop.entity.ProductOrder;

import java.util.Map;

public interface ProductOrderManager {
    boolean addProductOrder(ProductOrder productOrder);
    ProductOrder findByOrderNoAndAccountNo(String orderNo,Long accountNo);
    boolean updateOrderPaymentState(String orderNo, Long accountNo, String newState,String oldState);
    Map<String,Object> page(int page, int size, Long accountNo, String state);
    boolean deleteProductOrder(Long productOrderId, Long accountNo);
}
