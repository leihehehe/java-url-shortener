package com.leih.url.shop;

import com.leih.url.common.util.CommonUtil;
import com.leih.url.shop.ShopApplication;
import com.leih.url.shop.entity.ProductOrder;
import com.leih.url.shop.manager.ProductOrderManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ShopApplication.class)
@Slf4j
public class ShopTest {
    @Autowired
    ProductOrderManager productOrderManager;
    @Test
    public void testAdd(){
        for(long i =0;i<10;i++){
            ProductOrder productOrder = ProductOrder.builder().orderNo(CommonUtil.generateUUID())
                    .payPrice(11.0)
                    .state("NEW")
                    .nickname("shulei")
                    .accountNo(i)
                    .del(0)
                    .productId(2L)
                    .build();
            productOrderManager.addProductOrder(productOrder);
        }
    }
    @Test
    public void testPage(){
        Map<String, Object> page = productOrderManager.page(0, 10, 1L,null);
        log.info("{}",page);
    }
}
