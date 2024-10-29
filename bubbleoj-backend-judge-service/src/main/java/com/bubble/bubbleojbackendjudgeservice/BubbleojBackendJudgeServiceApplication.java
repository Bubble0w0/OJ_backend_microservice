package com.bubble.bubbleojbackendjudgeservice;

import com.bubble.bubbleojbackendjudgeservice.rabbitmq.initRabbitMq;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@ComponentScan("com.bubble")
@EnableDiscoveryClient
@EnableFeignClients("com.bubble.bubbleojbackendserviceclient.service")
public class BubbleojBackendJudgeServiceApplication {

    public static void main(String[] args) {
        //初始化消息队列
        initRabbitMq.doInit();
        SpringApplication.run(BubbleojBackendJudgeServiceApplication.class, args);
    }

}
