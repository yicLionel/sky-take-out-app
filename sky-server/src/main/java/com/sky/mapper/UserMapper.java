package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface UserMapper {
    @Select("select * from `user` where openid = #{openid}")
    User getByOpenid(@Param("openid") String openid);

    @Insert("""
            insert into `user` (openid, name, phone, avatar, create_time)
            values (#{openid}, #{name}, #{phone}, #{avatar}, #{createTime})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(User user);
}
