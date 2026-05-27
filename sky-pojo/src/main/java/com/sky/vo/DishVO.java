package com.sky.vo;

import com.sky.entity.DishFlavor;

import java.math.BigDecimal;
import java.util.List;

public class DishVO {
    public Long id;
    public String name;
    public Long categoryId;
    public BigDecimal price;
    public String image;
    public String description;
    public Integer status;
    public List<DishFlavor> flavors;
}
