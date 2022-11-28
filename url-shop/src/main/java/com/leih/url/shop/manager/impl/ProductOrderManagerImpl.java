package com.leih.url.shop.manager.impl;

import com.leih.url.shop.dao.ProductOrderRepository;
import com.leih.url.shop.entity.ProductOrder;
import com.leih.url.shop.manager.ProductOrderManager;
import com.leih.url.shop.vo.ProductOrderVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ProductOrderManagerImpl implements ProductOrderManager {
    @Autowired
    private ProductOrderRepository productOrderRepository;
    @Override
    public boolean addProductOrder(ProductOrder productOrder) {
        try{
            productOrderRepository.save(productOrder);
            return true;
        }catch (Exception e){
            log.error("Failed to save product order: {}",e.getMessage());
            return false;
        }
    }

    @Override
    public ProductOrder findByOrderNoAndAccountNo(String orderNo, Long accountNo) {
        return productOrderRepository.findByOrderNoAndAccountNoAndDel(orderNo,accountNo,0);
    }

    @Override
    public boolean updateOrderPaymentState(String orderNo, Long accountNo, String newState, String oldState) {
        try{
            productOrderRepository.updatePaymentState(orderNo, accountNo, newState, oldState);
            return true;
        }catch (Exception e){
            log.error("Failed to update payment state for product: {}",e.getMessage());
            return false;
        }
    }

    @Override
    public Map<String, Object> page(int page, int size, Long accountNo, String state) {
        Page<ProductOrder> pages;
        PageRequest pageRequest = PageRequest.of(page, size);
        if(!StringUtils.hasLength(state) || state.equalsIgnoreCase("all")){
             pages= productOrderRepository.findAllByAccountNoAndDelOrderByCreateTimeDesc(pageRequest, accountNo,0);
        }else{
            pages=productOrderRepository.findAllByAccountNoAndStateAndDelOrderByCreateTimeDesc(pageRequest,accountNo,state,0);
        }
        Map<String, Object> pageInfo = new HashMap<>(3);
        pageInfo.put("total_records", pages.getTotalElements());
        pageInfo.put("total_pages", pages.getTotalPages());
        pageInfo.put(
                "current_data",
                pages.getContent().stream()
                        .map(this::convertProductOrderToVo)
                        .collect(Collectors.toList()));
        return pageInfo;
    }

    @Override
    public boolean deleteProductOrder(Long productOrderId, Long accountNo) {
        try{
            productOrderRepository.deleteProductOrder(productOrderId,accountNo);
            return true;
        }catch (Exception e){
            return false;
        }

    }

    public ProductOrderVo convertProductOrderToVo(ProductOrder productOrder){
        ProductOrderVo productOrderVo = new ProductOrderVo();
        BeanUtils.copyProperties(productOrder,productOrderVo);
        return productOrderVo;
    }
}
