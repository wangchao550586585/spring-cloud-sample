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
@Table(name="tb_order")
public class Order {
    @Id
    private String orderId;// 订单号：用户id+时间戳(用户id+店铺id+时间戳)
    private String payment;// 实付金额。精确到2位小数;单位:元。如:200.07，表示:200元7分
    private Integer paymentType;// '支付类型，1、在线支付，2、货到付款',
    private String postFee;// 邮箱
    private Integer status;
}
