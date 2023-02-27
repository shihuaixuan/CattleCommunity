package com.twenty.cattlecommuntiy.controller.Interceptor;

import com.twenty.cattlecommuntiy.annotation.LoginRequrie;
import com.twenty.cattlecommuntiy.entity.User;
import com.twenty.cattlecommuntiy.util.HostHonder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * 只有登入的才能访问一些页面
 * 如：http://localhost:8080/community/user/setting->修改密码。
 * 若没有登入则无法访问
 *
 */
@Component
public class LoginRequireinterceptor implements HandlerInterceptor {
    
    @Autowired
    private HostHonder hostHonder;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(handler instanceof HandlerMethod){
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            LoginRequrie loginreqire = method.getAnnotation(LoginRequrie.class);
            if(loginreqire != null && hostHonder.getUser() == null){
                //若未登入则重定向到登入界面
                response.sendRedirect(request.getContextPath() + "/login");
                return false;
            }
        }
        return true;
    }
}
