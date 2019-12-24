package com.changgou.business.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * @Author:Administrator
 * @Date: 2019/12/23 20:01
 */
@Component
public class AdListener {

    @Autowired
     private RestTemplate restTemplate;

    @RabbitListener(queues = "ad_update_queue")
    public void receiveMessage(String message){
        System.out.println("接收到的消息为:"+ message);

        // 远程调用Nginx中的ad_update方法
        String url = "http://192.168.200.128/ad_update?position="+message;
        Map forObject = restTemplate.getForObject(url, Map.class);
        System.out.println("请求成功:"+forObject);
    }
}
