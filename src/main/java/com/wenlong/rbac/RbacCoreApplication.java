package com.wenlong.rbac;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class RbacCoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(RbacCoreApplication.class, args);
    }

}

