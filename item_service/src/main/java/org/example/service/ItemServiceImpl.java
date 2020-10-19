package org.example.service;

import org.example.dao.ItemDao;
import org.example.entity.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author WangChao
 * @create 2020/6/12 18:02
 */
@Service
public class ItemServiceImpl implements ItemService {
    @Autowired
    private ItemDao itemDao;
    @Override
    public Item findById(Long id) {
        return itemDao.findById(id).get();
    }
    @Override
    public void save(Item item) {
        itemDao.save(item);
    }

    @Override
    public void update(Item item) {
        itemDao.save(item);
    }
    @Override
    public void deleteById(Long id) {
        itemDao.deleteById(id);
    }
}
