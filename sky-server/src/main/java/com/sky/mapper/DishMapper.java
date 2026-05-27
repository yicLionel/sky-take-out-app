package com.sky.mapper;

import com.sky.entity.Dish;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface DishMapper {
    @Select("select * from dish where category_id = #{categoryId} and status = 1 order by id asc")
    List<Dish> listByCategoryId(@Param("categoryId") Long categoryId);

    @Select("select * from dish where id = #{id}")
    Dish getById(@Param("id") Long id);
}
