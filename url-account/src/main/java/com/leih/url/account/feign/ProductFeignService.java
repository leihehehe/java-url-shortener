package com.leih.url.account.feign;

import com.leih.url.common.util.JsonData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("url-shop-service")
public interface ProductFeignService {
    /**
     * Get product details
     * @param productId
     * @return
     */
    @GetMapping("/api/product/v1/details/{product_id}")
    JsonData getProductDetail(@PathVariable("product_id") Long productId);
}
