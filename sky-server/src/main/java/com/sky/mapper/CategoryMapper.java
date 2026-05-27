package com.sky.mapper;

import com.sky.entity.Category;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface CategoryMapper {
    @Select("select * from category where status = #{status} order by sort asc, id asc")
    List<Category> list(@Param("status") Integer status);
}
