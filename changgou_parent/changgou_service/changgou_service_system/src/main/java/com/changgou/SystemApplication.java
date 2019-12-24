package com.changgou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@EnableEurekaClient
@MapperScan(basePackages = {"com.changgou.system.dao"})
@ComponentScan(basePackages = "com.changgou.system")
@ComponentScan(basePackages = "com.changgou.common")
public class SystemApplication {
    public static void main(String[] args) {
        SpringApplication.run( SystemApplication.class);
    }
}
