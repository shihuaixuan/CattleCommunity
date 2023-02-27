package com.twenty.cattlecommuntiy.service;

import com.twenty.cattlecommuntiy.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
* @author twenty
* @description 针对表【user】的数据库操作Service
* @createDate 2023-01-05 09:06:57
*/
public interface UserService extends IService<User> {
    /**
     * 查找用户
     * @param id
     * @return
     */
    User findUserById(int id);

    /**
     * 用户注册
     * @return
     */
    Map<String, Object> register(User user);
    /**
     * 账户激活
     */
    int activation(int userId, String code);

    /**
     * 用户头像更新
     * @param userId
     * @param headerUrl
     * @return
     */
    int updateHeaderUrl(int userId, String headerUrl);

    /**
     * 修改密码
     * @param userId
     * @param password
     * @return
     */
    int updatePassword(int userId, String password);

    /**
     * 跟新salt
     * @param userId
     * @param salt
     * @return
     */
    int updateSalt(int userId, String salt);
    /**
     * 通过名字查找用户
     */
    User findUserByName(String username);
}
