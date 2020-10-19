package org.example.controller;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.hystrix.contrib.javanica.annotation.DefaultProperties;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.ribbon.proxy.annotation.Hystrix;
import org.example.OrderCommand;
import org.example.entity.Order;
import org.example.entity.User;
import org.example.feign.UserFeignClient;
import org.example.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * @author WangChao
 * @create 2020/6/12 18:06
 */
@RestController
@RequestMapping("/order")
//@DefaultProperties(defaultFallback = "defaultFallback")
public class OrderController {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private OrderService orderService;
    //使用spring提供的,从eureka中获取元数据的工具类
    @Autowired
    private DiscoveryClient discoveryClient;
    @Autowired
    private UserFeignClient userFeignClient;


    //配置熔断保护降级方法
    @HystrixCommand(fallbackMethod = "orderFallBack")
    //@HystrixCommand()   //使用统一降级方法
    @GetMapping("/{id}")
    public User findById(@PathVariable String id) throws Exception {
        //1:基于http调用
        // User user = restTemplate.getForObject("http://127.0.0.1:9001/user/1", User.class);

        //2:基于元数据调用
       // List<ServiceInstance> instances = discoveryClient.getInstances("service-user");
        //ServiceInstance instanceInfo = instances.get(0);
        //  User  user = restTemplate.getForObject("http://" + instanceInfo.getHost() + ":" + instanceInfo.getPort() + "/user/1", User.class);

        //3:使用ribbon调用,根据服务名获取
        User user = restTemplate.getForObject("http://service-user/user/1", User.class);
        //Order byId = orderService.findById(id);

        //4:feign调用
        //User user = userFeignClient.findById(1L);

        //5:自定义熔断,以及线程池隔离调用
        //User user = new OrderCommand(restTemplate,1L).execute();

     /*   if (id.equals("1")){
            throw  new Exception();
        }*/
        return user;
    }

    /**
     * 降级方法
     *  和需要受到保护的方法返回值,参数一致
     *
     * @param id
     * @return
     */
    public User orderFallBack(String id) {
        User user = new User();
        user.setEmail("降级咯");
        return user;
    }

    /**
     * 指定统一降级方法
     * 没有参数
     * 返回值只能User有用
     * @return
     */
    public User defaultFallback() {
        User user = new User();
        user.setEmail("统一降级方法");
        return user;
    }
    @PostMapping
    public void save(@RequestBody Order order) {
        orderService.save(order);
    }

    @PutMapping
    public void update(@RequestBody Order order) {
        orderService.save(order);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        orderService.deleteById(id);
    }
}
