package org.example.service;

import org.example.dao.OrderDao;
import org.example.entity.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author WangChao
 * @create 2020/6/12 18:02
 */
@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderDao orderDao;
    @Override
    public Order findById(String id) {
        return orderDao.findById(id).get();
    }
    @Override
    public void save(Order order) {
        orderDao.save(order);
    }

    @Override
    public void update(Order order) {
        orderDao.save(order);
    }
    @Override
    public void deleteById(String id) {
        orderDao.deleteById(id);
    }
}
