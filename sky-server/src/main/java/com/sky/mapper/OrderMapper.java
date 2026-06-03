package com.sky.mapper;

import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface OrderMapper {
    @Insert("""
            insert into `orders`
            (number, status, user_id, address_book_id, order_time, checkout_time, pay_status, amount,
             remark, phone, address, consignee, cancel_reason, rejection_reason, delivery_time,
             estimated_delivery_time, pack_amount, tableware_number, tableware_status)
            values
            (#{number}, #{status}, #{userId}, #{addressBookId}, #{orderTime}, #{checkoutTime}, #{payStatus}, #{amount},
             #{remark}, #{phone}, #{address}, #{consignee}, #{cancelReason}, #{rejectionReason}, #{deliveryTime},
             #{estimatedDeliveryTime}, #{packAmount}, #{tablewareNumber}, #{tablewareStatus})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Orders orders);

    @Select("select * from `orders` where id = #{id} and user_id = #{userId}")
    Orders getByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);

    @Select("select * from `orders` where user_id = #{userId} order by order_time desc, id desc")
    List<Orders> listByUserId(@Param("userId") Long userId);

    @Select("select * from `orders` order by order_time desc, id desc")
    List<Orders> listAll();

    @Select("select * from `orders` where id = #{id}")
    Orders getById(@Param("id") Long id);

    @Update("""
            update `orders`
            set status = #{status},
                pay_status = #{payStatus},
                checkout_time = #{checkoutTime}
            where id = #{id} and user_id = #{userId}
            """)
    void updatePayment(Orders orders);

    @Update("update `orders` set status = #{status} where id = #{id}")
    void updateStatus(@Param("id") Long id, @Param("status") Integer status);

    @Delete("delete from `orders` where id = #{id}")
    void deleteById(@Param("id") Long id);
}
