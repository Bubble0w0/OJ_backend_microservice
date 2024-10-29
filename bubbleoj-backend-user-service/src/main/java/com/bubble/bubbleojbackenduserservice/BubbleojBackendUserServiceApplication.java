package com.bubble.bubbleojbackenduserservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@MapperScan("com.bubble.bubbleojbackenduserservice.mapper")
@EnableScheduling
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@ComponentScan("com.bubble")
@EnableDiscoveryClient
@EnableFeignClients("com.bubble.bubbleojbackendserviceclient.service")
public class BubbleojBackendUserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BubbleojBackendUserServiceApplication.class, args);
    }

}
