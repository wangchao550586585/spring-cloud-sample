package org.example.feign;

import org.example.entity.Item;
import org.springframework.stereotype.Component;

/**
 * @author WangChao
 * @create 2020/6/13 15:56
 */
@Component
public class ItemFeignClientCallback implements ItemFeignClient {
    /**
     * 熔断降级方法
     * @param id
     * @return
     */
    @Override
    public Item findById(Long id) {
        Item user = new Item();
        user.setBarcode("对Feign的支持");
        return user;
    }
}
