package com.twenty.cattlecommuntiy.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.twenty.cattlecommuntiy.entity.LoginTicket;
import com.twenty.cattlecommuntiy.entity.User;
import com.twenty.cattlecommuntiy.mapper.UserMapper;
import com.twenty.cattlecommuntiy.service.LoginTicketService;
import com.twenty.cattlecommuntiy.mapper.LoginTicketMapper;
import com.twenty.cattlecommuntiy.util.CommunityUtil;
import com.twenty.cattlecommuntiy.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.jws.Oneway;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
* @author twenty
* @description 针对表【login_ticket】的数据库操作Service实现
* @createDate 2023-01-07 14:13:59
*/
@Service
public class LoginTicketServiceImpl extends ServiceImpl<LoginTicketMapper, LoginTicket>
    implements LoginTicketService{
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private LoginTicketMapper loginTicketMapper;
    @Autowired
    protected RedisTemplate redisTemplate;


    /**
     * 登入
     * @param username
     * @param password
     * @param expeirdSeconds
     * @return
     */
    @Override
    public Map<String, Object> login(String username, String password, int expeirdSeconds) {
        Map<String, Object> map = new HashMap<>();
        //验证是否填写
        if(StringUtils.isBlank(username)){
            map.put("usernameMsg","请填写账号");
            return map;
        }
        if(StringUtils.isBlank(password)){
            map.put("passwordMsg","请输入密码");
            return map;
        }
        User user = userMapper.selectByName(username);
        //验证账号
        if(user==null){
            map.put("usernameMsg","账号不存在");
            return map;
        }
        //验证是否激活
        if(user.getStatus()==0){
            map.put("usernameMsg","账号未激活");
        }
        //验证密码
        password = CommunityUtil.MD5(password+user.getSalt());
        if(!user.getPassword().equals(password)){
            map.put("passwordMsg","密码错误");
            return map;
        }
        //获得登入凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setStatus(0);
        loginTicket.setTicket(CommunityUtil.genetateUUID());
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expeirdSeconds * 1000));
        //loginTicketMapper.insert(loginTicket);
        String ticketKey = RedisKeyUtil.getTicketKey(loginTicket.getTicket());
        redisTemplate.opsForValue().set(ticketKey, loginTicket);
        map.put("ticket",loginTicket.getTicket());
        return map;
    }

    /**
     * 退出
     * @param ticket
     */
    @Override
    public void loginout(String ticket) {
        //loginTicketMapper.updateStatus(tickrt,1);
        String ticketKey = RedisKeyUtil.getTicketKey(ticket);
        LoginTicket loginTicket = (LoginTicket)redisTemplate.opsForValue().get(ticketKey);
        loginTicket.setStatus(1);
        redisTemplate.opsForValue().set(ticketKey,loginTicket);
    }

    /**
     * 通过ticket查找LoginTicket
     * @param ticket
     * @return
     */
    @Override
    public LoginTicket findLoginTicket(String ticket) {
        //return loginTicketMapper.selectByTicket(ticket);
        String ticketKey = RedisKeyUtil.getTicketKey(ticket);
        return (LoginTicket) redisTemplate.opsForValue().get(ticketKey);
    }
}




