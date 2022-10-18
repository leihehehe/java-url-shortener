package com.leih.url.shop.manager.impl;

import com.leih.url.shop.dao.ProductRepository;
import com.leih.url.shop.entity.Product;
import com.leih.url.shop.manager.ProductManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class ProductManagerImpl implements ProductManager {
    @Autowired
    ProductRepository productRepository;
    @Override
    public List<Product> listProducts() {
        return productRepository.findAll();
    }

    @Override
    public Product getProductDetail(Long productId) {
        return productRepository.getById(productId);
    }
}
