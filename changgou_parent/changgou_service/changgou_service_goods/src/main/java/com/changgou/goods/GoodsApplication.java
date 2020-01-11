package com.changgou.goods;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

//@SpringCloudApplication
@SpringBootApplication
@EnableEurekaClient
@MapperScan(basePackages = {"com.changgou.goods.dao"})
@ComponentScan(basePackages = "com.changgou.common")
@ComponentScan(basePackages = "com.changgou.goods")
public class GoodsApplication {
    public static void main(String[] args) {
        SpringApplication.run( GoodsApplication.class);
    }
}
