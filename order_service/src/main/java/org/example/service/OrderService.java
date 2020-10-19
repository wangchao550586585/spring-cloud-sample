package org.example.service;

import org.example.entity.Order;

/**
 * @author WangChao
 * @create 2020/6/12 18:00
 */
public interface OrderService {
    Order findById(String id);

    void save(Order order);

    void update(Order order);

    void deleteById(String id);
}
