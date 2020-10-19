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
@Table(name="tb_user")
public class User {
    @Id
    private Long id;
    private String username;

    private String password;
    private String phone;
    private String email;
    private LocalDateTime created;
    private LocalDateTime updated;
}
