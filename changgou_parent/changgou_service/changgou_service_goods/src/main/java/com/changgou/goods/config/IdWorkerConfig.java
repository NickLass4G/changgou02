package com.changgou.goods.config;

import com.changgou.common.util.IdWorker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author:Administrator
 * @Date: 2019/12/21 18:37
 */
@Configuration
public class IdWorkerConfig {
    @Value("${workerId}")
    private Integer workerId;

    @Value("${datacenterId}")
    private Integer dataCenterId;

    @Bean
    public IdWorker idWorker(){
        return new IdWorker(workerId,dataCenterId);
    }
}
