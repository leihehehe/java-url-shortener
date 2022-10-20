package com.leih.url.shop.listener;

import com.leih.url.common.enums.BizCodeEnum;
import com.leih.url.common.exception.BizException;
import com.leih.url.common.model.EventMessage;
import com.leih.url.shop.service.ProductOrderService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RabbitListener(queuesToDeclare = {@Queue("order.close.queue")
,@Queue("order.update.queue")})
public class ProductOrderListener {
    @Autowired
    private ProductOrderService productOrderService;
    @RabbitHandler
    public void productOrderHandler(EventMessage eventMessage, Message message, Channel channel){
        log.info("Detected messages from ProductOrderListener: {}",message);
        try{
            productOrderService.handleProductOrderMsg(eventMessage);
        }
        catch (Exception e){
            log.error("Failed to consume the message: {}",eventMessage);
            throw new BizException(BizCodeEnum.MQ_CONSUMER_EXCEPTION);
        }
        log.info("Successfully consumed the message");
    }

}
