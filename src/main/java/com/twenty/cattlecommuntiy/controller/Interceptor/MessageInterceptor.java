package com.twenty.cattlecommuntiy.controller.Interceptor;

import com.twenty.cattlecommuntiy.entity.User;
import com.twenty.cattlecommuntiy.service.MessageService;
import com.twenty.cattlecommuntiy.util.HostHonder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class MessageInterceptor implements HandlerInterceptor {
    @Autowired
    private HostHonder hostHonder;
    @Autowired
    private MessageService messageService;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHonder.getUser();
        if(user != null && modelAndView != null){
            int letterUnRead = messageService.findselectLetterUnRead(user.getId(), null);
            int notReadNotice = messageService.findNotReadNotice(user.getId(), null);
            modelAndView.addObject("allUnReadCount", letterUnRead+notReadNotice);
        }
    }
}
