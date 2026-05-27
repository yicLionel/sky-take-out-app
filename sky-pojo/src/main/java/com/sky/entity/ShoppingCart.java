package com.sky.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ShoppingCart {
    public Long id;
    public Long userId;
    public String name;
    public String image;
    public Long dishId;
    public Long setmealId;
    public String dishFlavor;
    public Integer number;
    public BigDecimal amount;
    public LocalDateTime createTime;
}
