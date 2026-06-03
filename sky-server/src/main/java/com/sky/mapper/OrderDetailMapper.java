package com.sky.mapper;

import com.sky.entity.OrderDetail;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Delete;

import java.util.List;

public interface OrderDetailMapper {
    @Insert("""
            insert into order_detail
            (name, image, order_id, dish_id, setmeal_id, dish_flavor, number, amount)
            values
            (#{name}, #{image}, #{orderId}, #{dishId}, #{setmealId}, #{dishFlavor}, #{number}, #{amount})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(OrderDetail orderDetail);

    @Select("select * from order_detail where order_id = #{orderId} order by id asc")
    List<OrderDetail> listByOrderId(@Param("orderId") Long orderId);

    @Delete("delete from order_detail where order_id = #{orderId}")
    void deleteByOrderId(@Param("orderId") Long orderId);
}
