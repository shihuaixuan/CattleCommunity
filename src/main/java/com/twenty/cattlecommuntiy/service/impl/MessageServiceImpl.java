package com.twenty.cattlecommuntiy.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.twenty.cattlecommuntiy.entity.Message;
import com.twenty.cattlecommuntiy.service.MessageService;
import com.twenty.cattlecommuntiy.mapper.MessageMapper;
import com.twenty.cattlecommuntiy.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
* @author twenty
* @description 针对表【message】的数据库操作Service实现
* @createDate 2023-01-12 14:45:52
*/
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message>
    implements MessageService{

    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Override
    public List<Message> findselectConversations(int userId, int offset, int limit) {
        return messageMapper.selectConversations(userId, offset, limit);
    }

    @Override
    public int findselectConversationCount(int userId) {
        return messageMapper.selectConversationCount(userId);
    }

    @Override
    public List<Message> findselectLetters(String conversationId, int offset, int limit) {
        return messageMapper.selectLetters(conversationId, offset, limit);
    }

    @Override
    public int findselectLetterCount(String conversationId) {
        return messageMapper.selectLetterCount(conversationId);
    }

    @Override
    public int findselectLetterUnRead(int userId, String conversationId) {
        return messageMapper.selectLetterUnRead(userId, conversationId);
    }

    /**
     * 添加信息操作
     * @param message
     * @return
     */
    @Override
    public int addMessage(Message message) {
        message.setContent(HtmlUtils.htmlEscape(message.getContent()));
        message.setContent(sensitiveFilter.filter(message.getContent()));
        return messageMapper.insert(message);
    }

    /**
     * 消息已读操作
     * @param ids
     * @return
     */
    @Override
    public int readMessage(List<Integer> ids) {
        return messageMapper.updateStatus(ids, 1);
    }

    @Override
    public Message findLastNotice(int userId, String topic) {
        return messageMapper.selectLastNotice(userId, topic);
    }

    @Override
    public int findNoticCount(int userId, String topic) {
        return messageMapper.selectNoticeCount(userId, topic);
    }

    @Override
    public int findNotReadNotice(int userId, String topic) {
        return messageMapper.selectNotReadNotice(userId, topic);
    }

    @Override
    public List<Message> findNotices(int userId, String topic, int offset, int limit) {
        return messageMapper.selectNotices(userId, topic, offset, limit);
    }


}




