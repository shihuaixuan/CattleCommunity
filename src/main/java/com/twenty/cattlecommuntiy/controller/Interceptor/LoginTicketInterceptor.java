package com.twenty.cattlecommuntiy.controller.Interceptor;

import com.twenty.cattlecommuntiy.entity.LoginTicket;
import com.twenty.cattlecommuntiy.entity.User;
import com.twenty.cattlecommuntiy.service.LoginTicketService;
import com.twenty.cattlecommuntiy.service.UserService;
import com.twenty.cattlecommuntiy.util.CookieUttil;
import com.twenty.cattlecommuntiy.util.HostHonder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * 定义拦截器：在一个响应前检查是否登入，若登入则获得用户信息
 */
@Component
public class LoginTicketInterceptor implements HandlerInterceptor {
    @Autowired
    private LoginTicketService loginTicketService;
    @Autowired
    private UserService userService;
    @Autowired
    private HostHonder hostHonder;

    /**
     * 在请求开始时查询登入用户,并取出用户信息
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //查询cookie
        String ticket = CookieUttil.getValue(request, "ticket");
        if(ticket!=null){
            LoginTicket loginTicket = loginTicketService.findLoginTicket(ticket);
            //检查凭证是否有效
            if(loginTicket!=null && loginTicket.getStatus()==0 && loginTicket.getExpired().after(new Date())){
                //查询用户
                User user = userService.findUserById(loginTicket.getUserId());
                //在本次请求中持有用户信息
                hostHonder.setUser(user);
            }
        }
        return true;
    }

    /**
     * 将用户信息存储到Model中返回给浏览器
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHonder.getUser();
        if(user!=null && modelAndView!=null){
            modelAndView.addObject("loginUser",user);
        }
    }

    /**
     * 浏览器使用完后清理
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHonder.clear();
    }
}
