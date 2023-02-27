package com.twenty.cattlecommuntiy.mapper;

import com.twenty.cattlecommuntiy.entity.LoginTicket;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author twenty
* @description 针对表【login_ticket】的数据库操作Mapper
* @createDate 2023-01-07 14:13:59
* @Entity com.twenty.cattlecommuntiy.entity.LoginTicket
*/
@Mapper
@Deprecated
public interface LoginTicketMapper extends BaseMapper<LoginTicket> {
    /**
     * 跟新status
     * @param ticket
     * @param status
     * @return
     */
    int updateStatus(String ticket,int status);

    /**
     * 查找LoginTicket对象
     * @param ticket
     * @return
     */
    LoginTicket selectByTicket(String ticket);
}




