package org.example.dao;

import org.example.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author WangChao
 * @create 2020/6/12 17:58
 */
public interface ItemDao extends JpaRepository<Item,Long>, JpaSpecificationExecutor<Item> {
}
