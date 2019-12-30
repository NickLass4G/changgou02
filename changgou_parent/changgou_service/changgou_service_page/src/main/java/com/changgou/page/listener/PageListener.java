package com.changgou.page.listener;

import com.changgou.page.config.RabbitMQConfig;
import com.changgou.page.service.PageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author:Administrator
 * @Date: 2019/12/27 21:44
 */
@Component
@Slf4j
public class PageListener {

    @Autowired
    private PageService pageService;
    @RabbitListener(queues = RabbitMQConfig.PAGE_CREATE_QUEUE)
    public void receiveMessage(String spuId){
        log.info("接收到的消息为 : {}",spuId);
        System.out.println("接收到的消息为 : " + spuId);
        pageService.createItemPage(spuId);
    }
}
