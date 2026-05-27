package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface ShoppingCartMapper {
    @Select("select * from shopping_cart where user_id = #{userId} order by create_time asc, id asc")
    List<ShoppingCart> list(@Param("userId") Long userId);

    @Select("""
            select * from shopping_cart
            where user_id = #{userId} and dish_id = #{dishId} and dish_flavor = #{dishFlavor}
            limit 1
            """)
    ShoppingCart getExistingDish(@Param("userId") Long userId,
                                 @Param("dishId") Long dishId,
                                 @Param("dishFlavor") String dishFlavor);

    @Select("""
            select * from shopping_cart
            where user_id = #{userId} and setmeal_id = #{setmealId}
            limit 1
            """)
    ShoppingCart getExistingSetmeal(@Param("userId") Long userId, @Param("setmealId") Long setmealId);

    @Insert("""
            insert into shopping_cart
            (user_id, name, image, dish_id, setmeal_id, dish_flavor, number, amount, create_time)
            values
            (#{userId}, #{name}, #{image}, #{dishId}, #{setmealId}, #{dishFlavor}, #{number}, #{amount}, #{createTime})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(ShoppingCart shoppingCart);

    @Update("update shopping_cart set number = number + 1 where id = #{id} and user_id = #{userId}")
    void addNumber(@Param("id") Long id, @Param("userId") Long userId);

    @Update("update shopping_cart set number = number - 1 where id = #{id} and user_id = #{userId}")
    void subNumber(@Param("id") Long id, @Param("userId") Long userId);

    @Delete("delete from shopping_cart where id = #{id} and user_id = #{userId}")
    void deleteById(@Param("id") Long id, @Param("userId") Long userId);

    @Delete("delete from shopping_cart where user_id = #{userId}")
    void clean(@Param("userId") Long userId);
}
