package com.twenty.cattlecommuntiy.service;

import com.twenty.cattlecommuntiy.entity.LoginTicket;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
* @author twenty
* @description 针对表【login_ticket】的数据库操作Service
* @createDate 2023-01-07 14:13:59
*/


public interface LoginTicketService extends IService<LoginTicket> {
    /**
     * 登入
     * @param username
     * @param password
     * @param expeirdSeconds
     * @return
     */
    Map<String, Object> login(String username, String password, int expeirdSeconds);

    /**
     * 退出
     * @param tickrt
     */
    void loginout(String tickrt);

    /**
     * 通过ticket查找LoginTicket
     * @param ticket
     * @return
     */
    LoginTicket findLoginTicket(String ticket);
}
