package org.example.service;

import org.example.entity.Cart;

/**
 * @author WangChao
 * @create 2020/6/12 18:00
 */
public interface CartService {
    Cart findById(Long id);

    void save(Cart cart);

    void update(Cart cart);

    void deleteById(Long id);
}
