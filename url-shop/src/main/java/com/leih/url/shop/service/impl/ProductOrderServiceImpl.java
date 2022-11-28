package com.leih.url.shop.service.impl;

import com.leih.url.common.constant.RedisKey;
import com.leih.url.common.enums.*;
import com.leih.url.common.exception.BizException;
import com.leih.url.common.intercepter.LoginInterceptor;
import com.leih.url.common.model.EventMessage;
import com.leih.url.common.model.LoggedInUser;
import com.leih.url.common.util.CommonUtil;
import com.leih.url.common.util.JsonData;
import com.leih.url.common.util.JsonUtil;
import com.leih.url.shop.config.RabbitMQConfig;
import com.leih.url.shop.controller.request.CreateOrderRequest;
import com.leih.url.shop.controller.request.ProductOrderPageRequest;
import com.leih.url.shop.entity.Product;
import com.leih.url.shop.entity.ProductOrder;
import com.leih.url.shop.manager.ProductManager;
import com.leih.url.shop.manager.ProductOrderManager;
import com.leih.url.shop.service.ProductOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class ProductOrderServiceImpl implements ProductOrderService {
  @Autowired private ProductOrderManager productOrderManager;
  @Autowired private ProductManager productManager;
  @Autowired private RabbitTemplate rabbitTemplate;
  @Autowired private RabbitMQConfig rabbitMQConfig;

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
    if(createOrderRequest.getProductId()==1){
      return JsonData.buildResult(BizCodeEnum.ORDER_CREATE_PRICE_FAIL);
    }
    Product product = productManager.getProductDetail(createOrderRequest.getProductId());
    if (!checkPrice(product, createOrderRequest)) {
      log.error("Inconsistent price:{}", createOrderRequest);
      throw new BizException(BizCodeEnum.ORDER_CREATE_PRICE_FAIL);
    }
    // create order
    ProductOrder productOrder =
        createProductOrder(createOrderRequest, loggedInUser, orderNo, product);
    log.info("Order has been created: {}",productOrder);
    // create payment info and invoke third-party API to get redirect url
    // PaymentInfoVo paymentInfoVo =
    // PaymentInfoVo.builder().accountNo(loggedInUser.getAccountNo())
    //    .orderNo(orderNo)
    //    .clientType(createOrderRequest.getClientType())
    //    .productName(product.getName())
    //    .description("")
    //    .payPrice(createOrderRequest.getPayPrice())
    //    .orderPaymentTimeoutMills(TimeConstant.ORDER_PAYMENT_TIMEOUT_MILLS).build();
    EventMessage eventMessage =
        EventMessage.builder()
            .eventMessageType(EventMessageTypeEnum.PRODUCT_ORDER_NEW.name())
            .accountNo(loggedInUser.getAccountNo())
            .bizId(orderNo)
            .build();
    rabbitTemplate.convertAndSend(
        rabbitMQConfig.getOrderEventExchange(),
        rabbitMQConfig.getOrderCloseDelayRoutingKey(),
        eventMessage);
    return JsonData.buildSuccess();
  }

  @Override
  public boolean cancelProductOrder(EventMessage eventMessage) {
    String orderNo = eventMessage.getBizId();
    Long accountNo = eventMessage.getAccountNo();
    ProductOrder productOrder = productOrderManager.findByOrderNoAndAccountNo(orderNo, accountNo);
    if (productOrder == null) {
      log.warn("Order does not exist");
      return true;
    }
    if (productOrder.getState().equalsIgnoreCase(OrderStateEnum.PAID.name())) {
      log.info("Order has already been paid");
      return true;
    }
    if (productOrder.getState().equalsIgnoreCase(OrderStateEnum.NEW.name())) {
      //      PaymentInfoVo paymentInfoVo = PaymentInfoVo.builder()
      //              .paymentType(productOrder.getPaymentType())
      //              .accountNo(accountNo)
      //              .orderNo(orderNo).build();
      // check order from third-party platform
      String result = "";
      if (!StringUtils.hasLength(result)) {
        // order hasn't been paid
        productOrderManager.updateOrderPaymentState(
            orderNo, accountNo, OrderStateEnum.CANCELED.name(), OrderStateEnum.NEW.name());
        log.info("Order has been successfully canceled");
      } else {
        // order has been paid
        productOrderManager.updateOrderPaymentState(
            orderNo, accountNo, OrderStateEnum.PAID.name(), OrderStateEnum.NEW.name());
        log.info(
            "Order has already been paid but it seems that we failed to update the state, try to resolve this problem: {}",
            eventMessage);
        // TODO
      }
      return true;
    }
    // canceled
    log.info("Order has been canceled");
    return true;
  }

  /**
   * This method should be implemented by third-party platform(like PayPal).
   * This is just a dumb payment method
   * @param orderNo
   * @return
   */
  @Override
  public JsonData payOrder(String orderNo) {
    Long accountNo = LoginInterceptor.threadLocal.get().getAccountNo();
    ProductOrder productOrder = productOrderManager.findByOrderNoAndAccountNo(orderNo, accountNo);
    if(productOrder==null){
      return JsonData.buildResult(BizCodeEnum.ORDER_CONFIRM_NOT_EXIST);
    }
    if (productOrder.getState().equalsIgnoreCase(OrderStateEnum.CANCELED.name())){
      return JsonData.buildResult(BizCodeEnum.ORDER_ALREADY_CANCELLED);
    } else if (productOrder.getState().equalsIgnoreCase(OrderStateEnum.PAID.name())) {
      return JsonData.buildResult(BizCodeEnum.ORDER_ALREADY_PAID);
    }
    log.info("Assume the client has successfully paid the order");
    //our server's callback should be called(assume it is PayPal)
    //it may be called multiple times
    Map<String,String> paramMap = new HashMap<>();
    paramMap.put("orderNo",orderNo);
    paramMap.put("tradingStatus","SUCCESS");
    paramMap.put("accountNo", String.valueOf(accountNo));
    processOrderCallbackMsg(PaymentTypeEnum.PAYPAL,paramMap);
    return JsonData.buildSuccess();
  }
@Autowired
  RedisTemplate<Object,Object> redisTemplate;
  @Override
  @Transactional(rollbackFor = Exception.class,propagation = Propagation.REQUIRED)
  public JsonData processOrderCallbackMsg(PaymentTypeEnum paymentType,Map<String,String> paramMap){
    String orderNo = paramMap.get("orderNo");
    String tradingStatus = paramMap.get("tradingStatus");
    Long accountNo = Long.valueOf(paramMap.get("accountNo"));
    ProductOrder productOrder = productOrderManager.findByOrderNoAndAccountNo(orderNo, accountNo);
    Map<String,Object> content = new HashMap<>(4);
    content.put("orderNo",orderNo);
    content.put("buyNum",productOrder.getBuyNum());
    content.put("accountNo",accountNo);
    content.put("product",productOrder.getProductSnapshot());

    EventMessage eventMessage = EventMessage.builder().accountNo(accountNo)
            .bizId(orderNo)
            .messageId(orderNo)
            .content(JsonUtil.obj2Json(content))
            .eventMessageType(EventMessageTypeEnum.PRODUCT_ORDER_PAID.name()).build();
    if(paymentType.equals(PaymentTypeEnum.PAYPAL)){
      if("SUCCESS".equalsIgnoreCase(tradingStatus)){
        //use redis to make sure callback is only invoked once(some payment platform will send messages multiple times)
        Boolean ok = redisTemplate.opsForValue().setIfAbsent(String.format(RedisKey.ORDER_CALLBACK_KEY,orderNo), "ok", 2, TimeUnit.DAYS);
        if(Boolean.TRUE.equals(ok)){
          //send messages
          rabbitTemplate.convertAndSend(rabbitMQConfig.getOrderEventExchange(),rabbitMQConfig.getOrderUpdatePlanRoutingKey(),eventMessage);
          log.info("send message to update plan and order");
          return JsonData.buildSuccess();
        }

      }
    }
    return JsonData.buildResult(BizCodeEnum.PAY_ORDER_CALLBACK_NOT_SUCCESS);
  }

  @Override
  public void handleProductOrderMsg(EventMessage eventMessage) {
    String eventMessageType = eventMessage.getEventMessageType();
    try{
      if (eventMessageType.equalsIgnoreCase(EventMessageTypeEnum.PRODUCT_ORDER_NEW.name())){
        //cancel the order
        this.cancelProductOrder(eventMessage);
      } else if (eventMessageType.equalsIgnoreCase(EventMessageTypeEnum.PRODUCT_ORDER_PAID.name())) {
        //update the order status
        String orderNo = eventMessage.getBizId();
        Long accountNo = eventMessage.getAccountNo();
        productOrderManager.updateOrderPaymentState(
                orderNo, accountNo, OrderStateEnum.PAID.name(), OrderStateEnum.NEW.name());
        log.info("Order has been updated to paid: {}",eventMessage);
      }
    }
    catch (Exception e){
      log.error("Failed to consume message:{}",e.getMessage());
      throw new BizException(BizCodeEnum.MQ_CONSUMER_EXCEPTION);
    }
  }

  private ProductOrder createProductOrder(
      CreateOrderRequest createOrderRequest,
      LoggedInUser loggedInUser,
      String orderNo,
      Product product) {
    ProductOrder productOrder =
        ProductOrder.builder()
            // user
            .accountNo(loggedInUser.getAccountNo())
            .nickname(loggedInUser.getUsername())
            // product
            .productId(product.getId())
            .productName(product.getName())
            .productSnapshot(JsonUtil.obj2Json(product))
            .productPrice(product.getPrice())
            // order
            .buyNum(createOrderRequest.getBuyNum())
            .orderNo(orderNo)
            .createTime(new Timestamp(new Date().getTime()))
            .del(0)
            // price
            .totalPrice(createOrderRequest.getTotalPrice())
            .payPrice(createOrderRequest.getPayPrice())
            .state(OrderStateEnum.NEW.name())
            .paymentType(PaymentTypeEnum.valueOf(createOrderRequest.getPaymentType()).name())
            // bill
            .billType(BillTypeEnum.valueOf(createOrderRequest.getBillType()).name())
            .billHeader(createOrderRequest.getBillHeader())
            .billReceiverPhone(createOrderRequest.getBillReceiverPhone())
            .billReceiverEmail(createOrderRequest.getBillReceiverEmail())
            .billContent(createOrderRequest.getBillContent())
            .build();
    // TODO: invoke payment info

    productOrderManager.addProductOrder(productOrder);
    return productOrder;
  }

  public boolean checkPrice(Product product, CreateOrderRequest createOrderRequest) {
    Double expectedTotalPrice = Double.valueOf(createOrderRequest.getBuyNum()) * product.getPrice();
    log.info("expected total price:{}",expectedTotalPrice);
    log.info("actual total price:{}",createOrderRequest.getPayPrice());
    // deduct price if any vouchers here
    return expectedTotalPrice.compareTo(createOrderRequest.getPayPrice()) == 0;
  }
}
