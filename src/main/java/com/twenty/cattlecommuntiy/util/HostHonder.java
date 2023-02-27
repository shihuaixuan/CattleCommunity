package com.twenty.cattlecommuntiy.util;

import com.twenty.cattlecommuntiy.entity.User;
import org.springframework.stereotype.Component;

/**
 * 封装用户信息，用于代替session
 */
@Component
public class HostHonder {
    private ThreadLocal<User> users = new ThreadLocal<>();

    public void setUser(User user){
        users.set(user);
    }

    public User getUser(){
        return users.get();
    }

    public void clear(){
        users.remove();
    }

}
