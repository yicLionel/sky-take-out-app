package com.sky.mapper;

import com.sky.entity.AddressBook;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface AddressBookMapper {
    @Select("select * from address_book where user_id = #{userId} order by default_status desc, id desc")
    List<AddressBook> list(@Param("userId") Long userId);

    @Select("select * from address_book where id = #{id} and user_id = #{userId}")
    AddressBook getByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);

    @Select("select * from address_book where user_id = #{userId} and default_status = 1 limit 1")
    AddressBook getDefault(@Param("userId") Long userId);

    @Insert("""
            insert into address_book
            (user_id, consignee, sex, phone, province_name, city_name, district_name, detail, default_status)
            values
            (#{userId}, #{consignee}, #{sex}, #{phone}, #{provinceName}, #{cityName}, #{districtName}, #{detail}, #{defaultStatus})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(AddressBook addressBook);

    @Update("""
            update address_book
            set consignee = #{consignee},
                sex = #{sex},
                phone = #{phone},
                province_name = #{provinceName},
                city_name = #{cityName},
                district_name = #{districtName},
                detail = #{detail},
                default_status = #{defaultStatus}
            where id = #{id} and user_id = #{userId}
            """)
    void update(AddressBook addressBook);

    @Update("update address_book set default_status = 0 where user_id = #{userId}")
    void clearDefault(@Param("userId") Long userId);

    @Update("update address_book set default_status = 1 where id = #{id} and user_id = #{userId}")
    void setDefault(@Param("id") Long id, @Param("userId") Long userId);

    @Delete("delete from address_book where id = #{id} and user_id = #{userId}")
    void deleteById(@Param("id") Long id, @Param("userId") Long userId);
}
