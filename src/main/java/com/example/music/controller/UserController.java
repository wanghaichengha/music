package com.example.music.controller;

import com.example.music.config.AppConfig;
import com.example.music.mapper.UserMapper;
import com.example.music.model.User;
import com.example.music.tools.Constant;
import com.example.music.tools.ResponseBodyMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @RequestMapping("/login")
    public ResponseBodyMessage<User> login(@RequestParam String username, @RequestParam String password,
                              HttpServletRequest request){
        /*获取前端数据，并写入User对象中
        User userLogin = new User();
        userLogin.setUsername(username);
        userLogin.setPassword(password);
        //调用login方法，从数据库中查询数据*/
        User user = userMapper.selectByName(username);
        if (user == null){
            return new ResponseBodyMessage<>(-1,"用户名或密码错误！",user);
        }else {
            boolean flg = bCryptPasswordEncoder.matches(password,user.getPassword());
            //判断密码是否匹配
            if (!flg){
                return new ResponseBodyMessage<>(-1,"用户名或密码错误",user);
            }
            //登录成功，创建会话，并存储会话
            request.getSession(true).setAttribute(Constant.USERINFO_SESSION_KEY,user);
            return new ResponseBodyMessage<>(0,"登录成功！",user);
        }

    }
}
