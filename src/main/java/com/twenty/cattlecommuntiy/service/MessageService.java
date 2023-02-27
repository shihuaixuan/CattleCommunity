package com.twenty.cattlecommuntiy.service;

import com.twenty.cattlecommuntiy.entity.Message;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author twenty
* @description 针对表【message】的数据库操作Service
* @createDate 2023-01-12 14:45:52
*/
public interface MessageService extends IService<Message> {
    //查询当前用户的会话列表，针对每个会话只展示一条私信
    List<Message> findselectConversations(int userId, int offset, int limit);
    //查询当前用户的会话数量
    int findselectConversationCount(int userId);
    //查询某个会话所包含的私信列表
    List<Message> findselectLetters(String conversationId, int offset, int limit);
    //查询某个会话所包含的数量
    int findselectLetterCount(String conversationId);
    //查询未读私信数量
    int findselectLetterUnRead(int userId, String conversationId);
    //添加消息
    int addMessage(Message message);
    //已读消息
    int readMessage(List<Integer> ids);
    //查询一个主题最新的消息
    Message findLastNotice(int userId, String topic);
    //查询主题的信息
    int findNoticCount(int userId, String topic);
    //查询未读通知
    int findNotReadNotice(int userId, String topic);
    ////查询某个主题所包含的通知列表
    List<Message> findNotices(int userId, String topic, int offset, int limit);

}
