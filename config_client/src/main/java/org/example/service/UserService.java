package org.example.service;

import org.example.entity.User;

/**
 * @author WangChao
 * @create 2020/6/12 18:00
 */
public interface UserService {
    User findById(Long id);

    void save(User user);

    void update(User user);

    void deleteById(Long id);
}
