package com.example.music.mapper;

import com.example.music.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.jws.soap.SOAPBinding;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserMapperTest {

    @Resource
    private UserMapper userMapper;

    @Test
    void login() {
        User userLogin = new User();
        userLogin.setUsername("bit");
        userLogin.setPassword("123456");
        User user =userMapper.login(userLogin);
        System.out.println(user);
        Assertions.assertNotNull(user);

    }
}