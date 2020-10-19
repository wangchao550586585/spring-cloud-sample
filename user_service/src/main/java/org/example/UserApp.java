package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

/**
 * Hello world!
 */
@SpringBootApplication
@EntityScan("org.example.entity")
//@EnableEurekaClient
//@EnableDiscoveryClient
//上述2个任意一个均可,新版本支持可不写
public class UserApp {
    public static void main(String[] args) {
        SpringApplication.run(UserApp.class, args);
    }
}
