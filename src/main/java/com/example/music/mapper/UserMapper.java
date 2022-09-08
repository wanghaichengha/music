package com.example.music.mapper;

import com.example.music.model.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
     //输入用户名和用户密码登录
     User login(User loginUser);
     //更具用户名查询用户信息
     User selectByName(String username);
}


