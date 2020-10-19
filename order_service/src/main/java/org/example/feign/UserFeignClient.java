package org.example.feign;

import org.example.entity.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author WangChao
 * @create 2020/6/13 11:43
 */
//name:服务提供者名
    //fallback:熔断发生降级的方法实现类
@FeignClient(name = "service-user",fallback = UserFeignClientCallback.class)
public interface UserFeignClient {
    @GetMapping("/user/{id}")
    public User findById(@PathVariable("id") Long id) ;
}
