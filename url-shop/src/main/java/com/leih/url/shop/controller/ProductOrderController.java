package com.leih.url.shop.controller;

import com.leih.url.common.constant.RedisKey;
import com.leih.url.common.enums.BizCodeEnum;
import com.leih.url.common.enums.ClientTypeEnum;
import com.leih.url.common.enums.PaymentTypeEnum;
import com.leih.url.common.intercepter.LoginInterceptor;
import com.leih.url.common.util.CommonUtil;
import com.leih.url.common.util.JsonData;
import com.leih.url.shop.annotation.PreventRepeatSubmission;
import com.leih.url.shop.controller.request.CreateOrderRequest;
import com.leih.url.shop.controller.request.ProductOrderPageRequest;
import com.leih.url.shop.service.ProductOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/product_order/v1")
@Slf4j
public class ProductOrderController {
  @Autowired private ProductOrderService productOrderService;
  @Autowired
  StringRedisTemplate redisTemplate;

  /**
   * Get token before placing an order to prevent duplicate submissions
   * @return
   */
  @GetMapping("token")
  public JsonData getOrderToken(){
    Long accountNo = LoginInterceptor.threadLocal.get().getAccountNo();
    String token = CommonUtil.getStringNumRandom(32);
    String key = String.format(RedisKey.SUBMIT_ORDER_TOKEN_KEY, accountNo, token);
    redisTemplate.opsForValue().set(key, String.valueOf(Thread.currentThread().getId()),30, TimeUnit.MINUTES);
    return JsonData.buildSuccess(token);
  }

  /**
   * Paginate product orders
   * @param request
   * @return
   */
  @PostMapping("page")
  public JsonData paginateProductOrder(
          @RequestBody ProductOrderPageRequest request) {
    Map<String, Object> result = productOrderService.paginateProductOrder(request);
    return JsonData.buildSuccess(result);
  }

  /**
   * Get state of an order
   * @param orderNo
   * @return
   */
  @GetMapping("query_state")
  public JsonData queryState(@RequestParam(value = "order_no") String orderNo) {
    String state = productOrderService.queryProductOrderState(orderNo);
    return StringUtils.hasLength(state)
        ? JsonData.buildSuccess(state)
        : JsonData.buildResult(BizCodeEnum.ORDER_CONFIRM_NOT_EXIST);
  }

  /**
   * Create order
   * @param createOrderRequest
   * @param response
   * @return
   */
  @PostMapping("create_order")
  @PreventRepeatSubmission(limitType = PreventRepeatSubmission.Type.TOKEN)
  public JsonData createOrder(
      @RequestBody CreateOrderRequest createOrderRequest, HttpServletResponse response) {
    return productOrderService.createOrder(createOrderRequest);
  }

  /**
   * This method should be implemented by third-party platform(like PayPal).
   * @param orderNo
   * @return
   */
  @GetMapping("/pay_order/{orderNo}")
  public JsonData payOrder(@PathVariable("orderNo") String orderNo){
    return productOrderService.payOrder(orderNo);
  }
}
