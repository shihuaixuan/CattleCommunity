package com.twenty.cattlecommuntiy.mapper;

import com.twenty.cattlecommuntiy.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author twenty
* @description 针对表【user】的数据库操作Mapper
* @createDate 2023-01-05 09:06:57
* @Entity com.twenty.cattlecommuntiy.entity.User
*/
@Mapper
public interface UserMapper extends BaseMapper<User> {
    /**
     * 通过邮箱查找
     * @param email
     * @return
     */
    User selectByEmail(String email);

    /**
     * 通过名称查找
     * @param username
     * @return
     */
    User selectByName(String username);

    /**
     * 更新状态
     * @param id
     * @param status
     * @return
     */
    int updateStatus(int id, int status);

    /**
     * 更新密码
     * @param id
     * @param password
     * @return
     */
    int updatePassword(int id, String password);

    /**
     * 更新头像
     * @param id
     * @param headerUrl
     * @return
     */
    int updateHeader(int id, String headerUrl);

    /**
     * 跟新salt
     * @param id
     * @param salt
     * @return
     */
    int updateSalt(int id, String salt);
}




