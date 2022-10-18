package com.leih.url.shop.manager;

import com.leih.url.shop.entity.Product;
import com.leih.url.shop.vo.ProductVo;

import java.util.List;

public interface ProductManager {
    List<Product> listProducts();

    Product getProductDetail(Long productId);
}
