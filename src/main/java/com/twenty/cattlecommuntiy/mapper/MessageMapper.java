package com.twenty.cattlecommuntiy.mapper;

import com.twenty.cattlecommuntiy.entity.Message;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.omg.PortableInterceptor.INACTIVE;

import java.util.List;

/**
* @author twenty
* @description 针对表【message】的数据库操作Mapper
* @createDate 2023-01-12 14:45:52
* @Entity com.twenty.cattlecommuntiy.entity.Message
*/
@Mapper
public interface MessageMapper extends BaseMapper<Message> {
    //查询当前用户的会话列表，针对每个会话只展示一条私信
    List<Message> selectConversations(int userId, int offset, int limit);
    //查询当前用户的会话数量
    int selectConversationCount(int userId);
    //查询某个会话所包含的私信列表
    List<Message> selectLetters(String conversationId, int offset, int limit);
    //查询某个会话所包含的数量
    int selectLetterCount(String conversationId);
    //查询未读私信数量
    int selectLetterUnRead(int userId, String conversationId);

    //跟新状态
    int updateStatus(List<Integer> ids, int status);
    //查询某个主题最新的通知
    Message selectLastNotice(int userId, String topic);
    //查询某个主题所包含的信息
    int selectNoticeCount(int userId, String topic);
    //查询未读的通知
    int selectNotReadNotice(int userId, String topic);
    //查询某个主题所包含的通知列表
    List<Message> selectNotices(int userId, String topic, int offset, int limit);
}




