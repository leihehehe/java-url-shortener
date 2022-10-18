package com.leih.url.shop.service.impl;

import com.leih.url.common.constant.TimeConstant;
import com.leih.url.common.enums.BillTypeEnum;
import com.leih.url.common.enums.BizCodeEnum;
import com.leih.url.common.enums.OrderStateEnum;
import com.leih.url.common.enums.PaymentTypeEnum;
import com.leih.url.common.exception.BizException;
import com.leih.url.common.intercepter.LoginInterceptor;
import com.leih.url.common.model.LoggedInUser;
import com.leih.url.common.util.CommonUtil;
import com.leih.url.common.util.JsonData;
import com.leih.url.common.util.JsonUtil;
import com.leih.url.shop.controller.request.CreateOrderRequest;
import com.leih.url.shop.controller.request.ProductOrderPageRequest;
import com.leih.url.shop.entity.Product;
import com.leih.url.shop.entity.ProductOrder;
import com.leih.url.shop.manager.ProductManager;
import com.leih.url.shop.manager.ProductOrderManager;
import com.leih.url.shop.service.ProductOrderService;
import com.leih.url.shop.vo.PaymentInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
@Slf4j
@Service
public class ProductOrderServiceImpl implements ProductOrderService {
  @Autowired private ProductOrderManager productOrderManager;
  @Autowired private ProductManager productManager;

  @Override
  public Map<String, Object> paginateProductOrder(ProductOrderPageRequest request) {
    Long accountNo = LoginInterceptor.threadLocal.get().getAccountNo();

    return productOrderManager.page(
        request.getPage(), request.getSize(), accountNo, request.getState());
  }

  @Override
  public String queryProductOrderState(String orderNo) {
    Long accountNo = LoginInterceptor.threadLocal.get().getAccountNo();
    ProductOrder productOrder = productOrderManager.findByOrderNoAndAccountNo(orderNo, accountNo);
    if (productOrder == null) {
      return "";
    } else {
      return productOrder.getState();
    }
  }

  @Override
  public JsonData createOrder(CreateOrderRequest createOrderRequest) {
    LoggedInUser loggedInUser = LoginInterceptor.threadLocal.get();
    String orderNo = CommonUtil.getStringNumRandom(32);
    Product product = productManager.getProductDetail(createOrderRequest.getProductId());
    if(checkPrice(product,createOrderRequest)){
      log.error("Inconsistent price:{}",createOrderRequest);
      throw new BizException(BizCodeEnum.ORDER_CREATE_PRICE_FAIL);
    }
    //create order
    ProductOrder productOrder =createProductOrder(createOrderRequest,loggedInUser,orderNo,product);
    //create payment info
    PaymentInfo paymentInfo = PaymentInfo.builder().accountNo(loggedInUser.getAccountNo())
            .orderNo(orderNo)
            .clientType(createOrderRequest.getClientType())
            .productName(product.getName())
            .description("")
            .payPrice(createOrderRequest.getPayPrice())
            .orderPaymentTimeoutMills(TimeConstant.ORDER_PAYMENT_TIMEOUT_MILLS).build();

    return null;
  }

  private ProductOrder createProductOrder(CreateOrderRequest createOrderRequest, LoggedInUser loggedInUser, String orderNo, Product product) {
    ProductOrder productOrder = ProductOrder.builder()
            //user
            .accountNo(loggedInUser.getAccountNo())
            .nickname(loggedInUser.getUsername())
            //product
            .productId(product.getId())
            .productName(product.getName())
            .productSnapshot(JsonUtil.obj2Json(product))
            .productPrice(product.getPrice())
            //order
            .buyNum(createOrderRequest.getBuyNum())
            .orderNo(orderNo)
            .createTime(new Date())
            .del(0)
            //price
            .totalPrice(createOrderRequest.getTotalPrice())
            .payPrice(createOrderRequest.getPayPrice())
            .state(OrderStateEnum.NEW.name())
            .paymentType(PaymentTypeEnum.valueOf(createOrderRequest.getPaymentType()).name())
            //bill
            .billType(BillTypeEnum.valueOf(createOrderRequest.getBillType()).name())
            .billHeader(createOrderRequest.getBillHeader())
            .billReceiverPhone(createOrderRequest.getBillReceiverPhone())
            .billReceiverEmail(createOrderRequest.getBillReceiverEmail())
            .billContent(createOrderRequest.getBillContent()).build();
    //TODO: invoke payment info
    //TODO: send delayed messages to close the order

    productOrderManager.addProductOrder(productOrder);
    return productOrder;
  }

  public boolean checkPrice(Product product, CreateOrderRequest createOrderRequest) {
    Double expectedTotalPrice = Double.valueOf(createOrderRequest.getBuyNum()) * product.getPrice();
    //deduct price if any vouchers here
    return expectedTotalPrice.compareTo(createOrderRequest.getPayPrice()) == 0;
  }
}
