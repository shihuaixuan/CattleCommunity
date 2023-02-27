package com.twenty.cattlecommuntiy.event;

import com.alibaba.fastjson.JSONObject;
import com.twenty.cattlecommuntiy.entity.Event;
import com.twenty.cattlecommuntiy.entity.Message;
import com.twenty.cattlecommuntiy.service.MessageService;
import com.twenty.cattlecommuntiy.util.ActivateConstant;
import netscape.javascript.JSObject;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class EventConsumer implements ActivateConstant {
    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);

    @Autowired
    private MessageService messageService;

    /**
     * 消费队列内容，发送站内信息
     * @param record
     */
    @KafkaListener(topics = {TOPIC_COMMENT, TOPIC_LIKE, TOPIC_FOLLOW})
    private void handerSysMessages(ConsumerRecord record){
        if(record == null || record.value() == null){
            logger.error("消息的内容为空");
            return;
        }
        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if(event == null){
            logger.error("消息格式错误！");
            return;
        }
        //发送站内消息
        Message message = new Message();
        message.setFromId(SYSTEN_USER_ID);
        message.setToId(event.getEntityUserId());
        message.setStatus(0);
        message.setConversationId(event.getTopic());
        message.setCreateTime(new Date());

        Map<String, Object> content = new HashMap<>();
        content.put("userId", event.getUserId());
        content.put("entityType", event.getEntityType());
        content.put("entityId", event.getEntityId());
        if(!event.getData().isEmpty()){
            for (Map.Entry<String, Object> entry : event.getData().entrySet()){
                content.put(entry.getKey(), entry.getValue());
            }
        }
        message.setContent(JSONObject.toJSONString(content));
        messageService.addMessage(message);

    }
}
