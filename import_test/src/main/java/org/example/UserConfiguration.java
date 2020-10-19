package org.example;

import org.springframework.context.annotation.Bean;

/**
 * @author WangChao
 * @create 2020/6/12 23:20
 */
//没有配置注解
public class UserConfiguration {
    @Bean
    public User getUser(){
        User user = new User();
        user.setId(10L);
        return user;
    }
}
