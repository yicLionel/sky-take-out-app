package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface DishFlavorMapper {
    @Select("select * from dish_flavor where dish_id = #{dishId} order by id asc")
    List<DishFlavor> listByDishId(@Param("dishId") Long dishId);
}
