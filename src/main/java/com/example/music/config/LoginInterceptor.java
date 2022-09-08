package com.example.music.config;

import com.example.music.tools.Constant;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        HttpSession httpSession = request.getSession(false);
        if(httpSession!=null && httpSession.getAttribute(Constant.USERINFO_SESSION_KEY)!=null) {
            //此时是登录状态
            return true;
        }
        return false;
    }
}

