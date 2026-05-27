package com.sky.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Orders {
    public static final int PENDING_PAYMENT = 1;
    public static final int PAID = 2;
    public static final int COMPLETED = 3;
    public static final int CANCELLED = 4;

    public static final int UN_PAID = 0;
    public static final int PAY_SUCCESS = 1;

    public Long id;
    public String number;
    public Integer status;
    public Long userId;
    public Long addressBookId;
    public LocalDateTime orderTime;
    public LocalDateTime checkoutTime;
    public Integer payStatus;
    public BigDecimal amount;
    public String remark;
    public String phone;
    public String address;
    public String consignee;
    public String cancelReason;
    public String rejectionReason;
    public LocalDateTime deliveryTime;
    public LocalDateTime estimatedDeliveryTime;
    public BigDecimal packAmount;
    public Integer tablewareNumber;
    public Integer tablewareStatus;
}
