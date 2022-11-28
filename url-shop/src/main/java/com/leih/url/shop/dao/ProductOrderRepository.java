package com.leih.url.shop.dao;

import com.leih.url.shop.entity.Product;
import com.leih.url.shop.entity.ProductOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ProductOrderRepository extends JpaRepository<ProductOrder,Long> {
    ProductOrder findByOrderNoAndAccountNoAndDel(String orderNo, Long accountNo,int del);

    @Transactional
    @Modifying
    @Query("update ProductOrder set state=:newState where orderNo=:orderNo and accountNo=:accountNo and state=:oldState and del=0")

    int updatePaymentState(@Param("orderNo") String orderNo,@Param("accountNo") Long accountNo, @Param("newState")String newState, @Param("oldState")String oldState);
    Page<ProductOrder> findAllByAccountNoAndDelOrderByCreateTimeDesc(Pageable pageable, Long accountNo,int del);
    Page<ProductOrder> findAllByAccountNoAndStateAndDelOrderByCreateTimeDesc(Pageable pageable, Long accountNo,String state,int del);
    @Transactional
    @Modifying
    @Query("update ProductOrder set del=1 where id=:productOrderId and accountNo=:accountNo and del=0")

    int deleteProductOrder(@Param("productOrderId") Long productOrderId,@Param("accountNo") Long accountNo);
}
