package org.example;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author WangChao
 * @create 2020/6/12 17:22
 */
@Data
public class User {
    private Long id;
    private String username;

    private String password;
    private String phone;
    private String email;
    private LocalDateTime created;
    private LocalDateTime updated;
}
