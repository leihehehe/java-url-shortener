package com.leih.url.shop.controller;

import com.leih.url.common.util.JsonData;
import com.leih.url.shop.entity.Product;
import com.leih.url.shop.service.ProductService;
import com.leih.url.shop.vo.ProductVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product/v1")
public class ProductController {
  @Autowired private ProductService productService;

  /**
   * View all the products
   * @return
   */
  @GetMapping("list")
  public JsonData listProducts() {
    List<ProductVo> productVoList = productService.listProducts();
    return JsonData.buildSuccess(productVoList);
  }
  @GetMapping("details/{product_id}")
  public JsonData getProductDetail(@PathVariable("product_id") Long productId){
    ProductVo productVo = productService.getProductDetail(productId);
    return JsonData.buildSuccess(productVo);
  }
}
