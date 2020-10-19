package org.example.feign;

import org.example.entity.User;
import org.springframework.stereotype.Component;

/**
 * @author WangChao
 * @create 2020/6/13 15:56
 */
@Component
public class UserFeignClientCallback implements UserFeignClient {
    /**
     * 熔断降级方法
     * @param id
     * @return
     */
    @Override
    public User findById(Long id) {
        User user = new User();
        user.setEmail("对Feign的支持");
        return user;
    }
}
