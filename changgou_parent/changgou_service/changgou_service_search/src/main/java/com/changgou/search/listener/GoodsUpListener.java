package com.changgou.search.listener;

import com.changgou.search.config.RabbitMQConfig;
import com.changgou.search.service.EsManagerService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author:Administrator
 * @Date: 2019/12/23 22:58
 */
@Component
public class GoodsUpListener {
    @Autowired
    private EsManagerService esManagerService;
    @RabbitListener(queues = RabbitMQConfig.SEARCH_ADD_QUEUE)
    public void reciveMessage(String spuId){
        System.out.println("接收到的消息为:"+spuId);
        // 查询skuList并添加到索引库
        esManagerService.importToESBySpuId(spuId);
    }
}
