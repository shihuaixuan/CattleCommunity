package com.twenty.cattlecommuntiy.controller;

import com.twenty.cattlecommuntiy.entity.Event;
import com.twenty.cattlecommuntiy.entity.Page;
import com.twenty.cattlecommuntiy.entity.User;
import com.twenty.cattlecommuntiy.event.EventProducer;
import com.twenty.cattlecommuntiy.service.FollowService;
import com.twenty.cattlecommuntiy.util.ActivateConstant;
import com.twenty.cattlecommuntiy.util.CommunityUtil;
import com.twenty.cattlecommuntiy.util.HostHonder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
public class FollowController implements ActivateConstant {
    @Autowired
    private FollowService followService;
    @Autowired
    private HostHonder hostHonder;
    @Autowired
    private EventProducer producer;

    @PostMapping("/follow")
    @ResponseBody
    public String follow(int entityType, int entityId){
        User user = hostHonder.getUser();

        followService.follow(user.getId(), entityType, entityId);

        //关注后触发系统通知事件
        Event event = new Event()
                .setTopic(TOPIC_FOLLOW)
                .setUserId(user.getId())
                .setEntityType(entityType)
                .setEntityId(entityId)
                .setEntityUserId(entityId);
        producer.fireEvent(event);

        return CommunityUtil.getJSONString("0","已关注");
    }

    @PostMapping("/unfollow")
    @ResponseBody
    public String unfollow(int entityType, int entityId){
        User user = hostHonder.getUser();

        followService.unfollow(user.getId(), entityType, entityId);
        return CommunityUtil.getJSONString("0","已取消关注");
    }

    @GetMapping("/followees/{userId}")
    public String getFollowees(@PathVariable("userId") int userId, Page page, Model model){
        User user = hostHonder.getUser();
        if(user == null){
            throw new RuntimeException("用户不存在");
        }
        model.addAttribute("user", user);

        page.setLimit(5);
        page.setPath("/followees"+userId);
        page.setRows((int) followService.findFolloweeCount(userId, ENTITY_USER));
        //关注状态
        List<Map<String, Object>> followees = followService.findFollowees(userId, page.getOffset(), page.getLimit());
        if(followees != null){
            for(Map<String, Object> map : followees){
                User u = (User)map.get("user");
                map.put("hasFollowed", hasFollowed(u.getId()));
            }
        }
        model.addAttribute("users",followees);
        return "/site/followee";
    }

    @GetMapping("/followers/{userId}")
    public String getFollowers(@PathVariable("userId") int userId, Page page, Model model){
        User user = hostHonder.getUser();
        if(user == null){
            throw new RuntimeException("用户不存在");
        }
        model.addAttribute("user", user);

        page.setLimit(5);
        page.setPath("/followers"+userId);
        page.setRows((int) followService.findFollowerCount(ENTITY_USER, userId));
        //关注状态
        List<Map<String, Object>> followees = followService.findFollowers(userId, page.getOffset(), page.getLimit());
        if(followees != null){
            for(Map<String, Object> map : followees){
                User u = (User)map.get("user");
                map.put("hasFollowed", hasFollowed(u.getId()));
            }
        }
        model.addAttribute("users",followees);
        return "/site/follower";
    }
    private boolean hasFollowed(int userId){
        if(hostHonder.getUser() == null){
            return false;
        }
        return followService.hasFollow(hostHonder.getUser().getId(), ENTITY_USER, userId);
    }
}
