package com.twenty.cattlecommuntiy.event;

import com.alibaba.fastjson.JSONObject;
import com.twenty.cattlecommuntiy.entity.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class EventProducer {

    @Autowired
    private KafkaTemplate kafkaTemplate;

    /**
     * 处理事件
     * @param event
     */
    public void fireEvent(Event event){
        // 将事件发送到指定的主题
        kafkaTemplate.send(event.getTopic(), JSONObject.toJSONString(event));
    }
}
