package com.lunazkoe.naa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableFeignClients
@EnableScheduling
public class NewsAggregatorAdvV1Application {

    public static void main(String[] args) {
        SpringApplication.run(NewsAggregatorAdvV1Application.class, args);
    }

}
