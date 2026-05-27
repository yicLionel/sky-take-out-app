package com.sky.vo;

import com.sky.entity.OrderDetail;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderVO {
    public Long id;
    public String number;
    public Integer status;
    public Long userId;
    public LocalDateTime orderTime;
    public LocalDateTime checkoutTime;
    public Integer payStatus;
    public BigDecimal amount;
    public String remark;
    public String phone;
    public String address;
    public String consignee;
    public List<OrderDetail> orderDetails;
}
