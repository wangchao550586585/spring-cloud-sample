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
@Table(name="tb_item")
public class Item {
    @Id
    private Long id;
    private String title;
    private String sellPoint;
    private Long price;
    private Integer num;
    private String barcode;
    private String image;
    private Long cid;
    private Integer status;


}
