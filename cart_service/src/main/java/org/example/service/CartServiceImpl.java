package org.example.service;

import org.example.dao.CartDao;
import org.example.entity.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author WangChao
 * @create 2020/6/12 18:02
 */
@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private CartDao cartDao;
    @Override
    public Cart findById(Long id) {
        return cartDao.findById(id).get();
    }
    @Override
    public void save(Cart cart) {
        cartDao.save(cart);
    }

    @Override
    public void update(Cart cart) {
        cartDao.save(cart);
    }
    @Override
    public void deleteById(Long id) {
        cartDao.deleteById(id);
    }
}
