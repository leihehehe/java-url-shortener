package com.leih.url.shop.service.impl;

import com.leih.url.shop.entity.Product;
import com.leih.url.shop.manager.ProductManager;
import com.leih.url.shop.service.ProductService;
import com.leih.url.shop.vo.ProductVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductManager productManager;
    @Override
    public List<ProductVo> listProducts() {
        List<Product> products = productManager.listProducts();
        return products.stream().map(this::productToVo).collect(Collectors.toList());
    }

    @Override
    public ProductVo getProductDetail(Long productId) {
        Product product = productManager.getProductDetail(productId);
        return productToVo(product);
    }

    public ProductVo productToVo(Product product){
        ProductVo productVo = new ProductVo();
        BeanUtils.copyProperties(product,productVo);
        return productVo;
    }
}
