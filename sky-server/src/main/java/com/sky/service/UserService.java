package com.sky.service;

import com.sky.config.JwtProperties;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.mapper.UserMapper;
import com.sky.utils.JwtUtil;
import com.sky.vo.UserLoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtProperties jwtProperties;

    public UserLoginVO login(UserLoginDTO userLoginDTO) {
        String code = userLoginDTO == null ? null : userLoginDTO.code;
        if (code == null || code.isBlank()) {
            code = "demo";
        }
        String openid = "mock_openid_" + code.replaceAll("[^a-zA-Z0-9_-]", "_");
        if ("mock_openid_demo".equals(openid)) {
            openid = openid + "_" + UUID.randomUUID().toString().substring(0, 8);
        }
        User user = userMapper.getByOpenid(openid);
        if (user == null) {
            user = new User();
            user.openid = openid;
            user.name = "微信用户";
            user.avatar = "/images/avatar.svg";
            user.createTime = LocalDateTime.now();
            userMapper.insert(user);
        }
        UserLoginVO vo = new UserLoginVO();
        vo.id = user.id;
        vo.openid = user.openid;
        vo.token = JwtUtil.createToken(user.id, user.openid, jwtProperties.getSecret(), jwtProperties.getTtlSeconds());
        return vo;
    }
}
