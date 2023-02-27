package com.twenty.cattlecommuntiy.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.twenty.cattlecommuntiy.entity.User;
import com.twenty.cattlecommuntiy.service.UserService;
import com.twenty.cattlecommuntiy.mapper.UserMapper;
import com.twenty.cattlecommuntiy.util.ActivateConstant;
import com.twenty.cattlecommuntiy.util.CommunityUtil;
import com.twenty.cattlecommuntiy.util.MailClient;
import com.twenty.cattlecommuntiy.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
* @author twenty
* @description 针对表【user】的数据库操作Service实现
* @createDate 2023-01-05 09:06:57
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService , ActivateConstant {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MailClient mailClient;
    @Autowired
    private TemplateEngine templateEngine;
    @Autowired
    private RedisTemplate redisTemplate;
    @Value("${community.path.domain}")
    private String domain;
    @Value("${server.servlet.context-path}")
    private String contextpath;
    @Override
    public User findUserById(int id) {
        User user = getCache(id);
        if(user == null){
            user = initCache(id);
        }
        return user;
    }

    /**
     * 用户注册
     * @param user
     * @return
     */
    @Override
    public Map<String, Object> register(User user) {
        Map<String, Object> map = new HashMap<>();
        //数据为空
        if(user==null){
            throw new IllegalArgumentException("参数不能为空");
        }
        //判断用户是否为空
        if(StringUtils.isBlank(user.getUsername())){
            map.put("usernameMsg","用户名不能为空");
            return map;
        }
        //判断密码是否为空
        if(StringUtils.isBlank(user.getPassword())){
            map.put("passwordMsg","密码不能为空");
            return map;
        }
        //判断邮箱是否为空
        if(StringUtils.isBlank(user.getEmail())){
            map.put("emailMsg","邮箱不能为空");
            return map;
        }
        //判断用户名是否存在
        User u = userMapper.selectByName(user.getUsername());
        if(u!=null){
            map.put("usernameMsg","用户名已存在");
            return map;
        }
        //验证邮箱
        u = userMapper.selectByEmail(user.getEmail());
        if(u!=null){
            map.put("emailMsg","邮箱已被注册");
            return map;
        }
        //注册用户
        user.setSalt(CommunityUtil.genetateUUID().substring(0,5));
        user.setPassword(CommunityUtil.MD5(user.getPassword()+user.getSalt()));
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(CommunityUtil.genetateUUID());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userMapper.insert(user);
        //发送激活代码邮件
        Context context = new Context();
        context.setVariable("email",user.getEmail());
        //激活码连接->http://localhost8080/community/activation/101/code
        String url = domain + contextpath + "/activation/" + user.getId() +"/"+user.getActivationCode();
        context.setVariable("url",url);
        //html页面
        String process = templateEngine.process("/mail/activation", context);
        mailClient.sendMail(user.getEmail(),"激活账户",process);
        return map;
    }

    /**
     * 账户激活
     * @param userId
     * @param code
     * @return
     */
    @Override
    public int activation(int userId, String code) {
        User user = userMapper.selectById(userId);
        if(user.getStatus()==1){
            return ACTIVATE_REPLAY;
        }else if(user.getActivationCode().equals(code)){
            userMapper.updateStatus(userId,1);
            deleteCache(userId);
            return ACTIVATE_SUCCESS;
        }else{
            return ACTIVATE_FAILURE;
        }
    }

    /**
     * 更新头像
     * @param userId
     * @param headerUrl
     * @return
     */
    public int updateHeaderUrl(int userId, String headerUrl){
        int i = userMapper.updateHeader(userId, headerUrl);
        deleteCache(userId);
        return i;
    }

    /**
     * 修改密码
     * @param userId
     * @param password
     * @return
     */
    @Override
    public int updatePassword(int userId, String password) {
        int i = userMapper.updatePassword(userId, password);
        deleteCache(userId);
        return i;
    }

    /***
     * 跟新salt
     * @param userId
     * @param salt
     * @return
     */
    @Override
    public int updateSalt(int userId, String salt) {
        int i = userMapper.updateSalt(userId, salt);
        deleteCache(userId);
        return i;
    }

    @Override
    public User findUserByName(String username) {
        return userMapper.selectByName(username);
    }

    //1.从缓存中取值
    private User getCache(int userId){
        String userKey = RedisKeyUtil.getUserKey(userId);
        return (User) redisTemplate.opsForValue().get(userKey);
    }
    //2. 取不到时初始化缓存数据
    private User initCache(int userId){
        User user = userMapper.selectById(userId);
        String userKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.opsForValue().set(userKey, user);
        return user;
    }
    //3. 数据变更清除缓存
    private void deleteCache(int userId){
        String userKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.delete(userKey);
    }

}




