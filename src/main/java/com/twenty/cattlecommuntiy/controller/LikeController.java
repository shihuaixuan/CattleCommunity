package com.twenty.cattlecommuntiy.controller;

import com.twenty.cattlecommuntiy.entity.Event;
import com.twenty.cattlecommuntiy.entity.User;
import com.twenty.cattlecommuntiy.event.EventProducer;
import com.twenty.cattlecommuntiy.service.LikeService;
import com.twenty.cattlecommuntiy.util.ActivateConstant;
import com.twenty.cattlecommuntiy.util.CommunityUtil;
import com.twenty.cattlecommuntiy.util.HostHonder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class LikeController implements ActivateConstant {

    @Autowired
    private LikeService likeService;
    @Autowired
    private HostHonder hostHonder;
    @Autowired
    private EventProducer eventProducer;

    @PostMapping("/like")
    @ResponseBody
    public String like(int entityType, int entityId, int entityUserId, int postId){
        User user = hostHonder.getUser();

        likeService.like(user.getId(),entityType, entityId,entityUserId);
        long likeCount = likeService.findEntityLikeCount(entityType, entityId);
        int likeStatus = likeService.findEntityLikeStatus(user.getId(),entityType, entityId);

        Map<String, Object> map = new HashMap<>();
        map.put("likeCount",likeCount);
        map.put("likeStatus",likeStatus);

        //点赞后触发系统通知事件
        if(likeStatus == 1) {
            Event event = new Event()
                    .setTopic(TOPIC_LIKE)
                    .setUserId(hostHonder.getUser().getId())
                    .setEntityType(entityType)
                    .setEntityId(entityId)
                    .setEntityUserId(entityUserId)
                    .setData("postId", postId);
            eventProducer.fireEvent(event);
        }

        return CommunityUtil.getJSONString("0", null, map);

    }
}
