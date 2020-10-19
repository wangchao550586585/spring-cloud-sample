package org.example.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * @author WangChao
 * @create 2020/6/12 17:22
 */
@Data
@Entity
@Table(name="tb_cart")
public class Cart {
    @Id
    private Long id;
    private Long userId;
    private Long itemId;
    private String itemTitle;
    private String itemImage;
    private Long itemPrice;
    private Integer num;

}
