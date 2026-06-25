package com.collectorhub;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy(exposeProxy = true)
@MapperScan("com.collectorhub.mapper")
@SpringBootApplication
public class CollectorHubApplication {

    public static void main(String[] args) {
        SpringApplication.run(CollectorHubApplication.class, args);
    }

}
