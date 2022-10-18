package com.leih.url.shop.service;

import com.leih.url.shop.vo.ProductVo;

import java.util.List;

public interface ProductService {
    List<ProductVo> listProducts();

    ProductVo getProductDetail(Long productId);
}
