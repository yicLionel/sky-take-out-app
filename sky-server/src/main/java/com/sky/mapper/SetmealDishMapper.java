package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface SetmealDishMapper {
    @Select("select * from setmeal_dish where setmeal_id = #{setmealId} order by id asc")
    List<SetmealDish> listBySetmealId(@Param("setmealId") Long setmealId);
}
