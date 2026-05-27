package com.sky.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrderSubmitVO {
    public Long id;
    public String orderNumber;
    public BigDecimal orderAmount;
    public LocalDateTime orderTime;
}
