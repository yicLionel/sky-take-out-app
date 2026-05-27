package com.sky.mapper;

import com.sky.entity.Setmeal;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface SetmealMapper {
    @Select("select * from setmeal where category_id = #{categoryId} and status = 1 order by id asc")
    List<Setmeal> listByCategoryId(@Param("categoryId") Long categoryId);

    @Select("select * from setmeal where id = #{id}")
    Setmeal getById(@Param("id") Long id);
}
