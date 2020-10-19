package org.example.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import org.example.entity.Cart;
import org.example.entity.Item;
import org.example.feign.ItemFeignClient;
import org.example.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

/**
 * @author WangChao
 * @create 2020/6/12 18:06
 */
@RestController
@RequestMapping("/cart")
public class CartController {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private CartService userService;
    @Autowired
    private ItemFeignClient itemFeignClient;
    /**
     * SentinelResource
     *          value:自定义资源名,默认全类名.方法名
     *
     * @param id
     * @return
     */
    //@SentinelResource(value = "itemfindById" ,blockHandler = "itemBlockHandler",fallback = "itemFallback")
    @GetMapping("/{id}")
    public Item findById(@PathVariable Long id) {

        //3:使用ribbon调用,根据服务名获取
        //Item item = restTemplate.getForObject("http://service-item/item/536563", Item.class);

        Item item = itemFeignClient.findById(536563L);
        Cart byId = userService.findById(id);
        return item;
    }

    /**
     * 定义降级规则
     *  hystrix和sentinel不同在于
     *      sentinel可以针对熔断和异常分别设置降级方法
     *
     * @param id
     * @return
     */
    public Item itemBlockHandler( Long id) {
        Item item = new Item();
        item.setBarcode("触发熔断的降级方法");
        return item;
    }
    public Item itemFallback( Long id) {
        Item item = new Item();
        item.setBarcode("触发异常执行的降级方法");
        return item;
    }

    @PostMapping
    public void save(@RequestBody Cart cart) {
        userService.save(cart);
    }

    @PutMapping
    public void update(@RequestBody Cart cart) {
        userService.save(cart);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        userService.deleteById(id);
    }
}
