package org.example.service;

import org.example.entity.Item;

/**
 * @author WangChao
 * @create 2020/6/12 18:00
 */
public interface ItemService {
    Item findById(Long id);

    void save(Item item);

    void update(Item item);

    void deleteById(Long id);
}
