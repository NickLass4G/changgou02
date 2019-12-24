package com.itheima.canal.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



/**
 * @Author:Administrator
 * @Date: 2019/12/23 20:33
 */
@Configuration
public class RabbitMQConfig {
    // 定义交换机名
    public static final String GOODS_UP_EXCHANGE = "goods_up_exchange"; // 商品上架交换机
    public static final String GOODS_DOWM_EXCHANGE = "goods_down_exchange"; // 商品下架交换机

    // 定义队列名
    public static final String AD_UPDATE_QUEUE = "ad_update_queue"; // 修改Redis缓存
    public static final String SEARCH_ADD_QUEUE = "search_add_queue"; // 商品上架队列
    public static final String SEARCH_DEL_QUEUE = "search_del_queue"; // 商品上架队列


    // 声明队列
    @Bean
    public Queue queue(){
        return new Queue(AD_UPDATE_QUEUE);
    }

    @Bean(SEARCH_ADD_QUEUE)
    public Queue SEARCH_ADD_QUEUE(){
        return new Queue(SEARCH_ADD_QUEUE);
    }

    @Bean(SEARCH_DEL_QUEUE)
    public Queue SEARCH_DEL_QUEUE(){
        return new Queue(SEARCH_DEL_QUEUE);
    }

    // 声明交换机
    @Bean(GOODS_UP_EXCHANGE)
    public Exchange GOODS_UP_EXCHANGE(){
        return ExchangeBuilder.fanoutExchange(GOODS_UP_EXCHANGE).durable(true).build();
    }
    @Bean(GOODS_DOWM_EXCHANGE)
    public Exchange GOODS_DOWM_EXCHANGE(){
        return ExchangeBuilder.fanoutExchange(GOODS_DOWM_EXCHANGE).durable(true).build();
    }
    // 将队列绑定到交换机
    @Bean
    public Binding GOODS_UP_EXCHANGE_BINGDING(@Qualifier(SEARCH_ADD_QUEUE) Queue queue,@Qualifier(GOODS_UP_EXCHANGE) Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with("").noargs();
    }

    @Bean
    public Binding GOODS_DOWN_EXCHANGE_BINGDING(@Qualifier(SEARCH_DEL_QUEUE) Queue queue,@Qualifier(GOODS_DOWM_EXCHANGE) Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with("").noargs();
    }
}
