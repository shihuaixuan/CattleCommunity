package com.twenty.cattlecommuntiy.controller;

import com.twenty.cattlecommuntiy.entity.Comment;
import com.twenty.cattlecommuntiy.entity.DiscussPost;
import com.twenty.cattlecommuntiy.entity.Event;
import com.twenty.cattlecommuntiy.event.EventProducer;
import com.twenty.cattlecommuntiy.service.CommentService;
import com.twenty.cattlecommuntiy.service.DiscussPostService;
import com.twenty.cattlecommuntiy.util.ActivateConstant;
import com.twenty.cattlecommuntiy.util.HostHonder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;

@Controller
@RequestMapping("/comment")
public class CommentController implements ActivateConstant {
    @Autowired
    private CommentService commentService;
    @Autowired
    private HostHonder hostHonder;
    @Autowired
    private EventProducer eventProducer;
    @Autowired
    private DiscussPostService discussPostService;
    @PostMapping("/add/{discussPostId}")
    private String addComment(@PathVariable("discussPostId") int discussPostId, Comment comment){
        comment.setUserId(hostHonder.getUser().getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        commentService.addComment(comment);

        //评论后触发系统通知事件
        Event event = new Event()
                .setTopic(TOPIC_COMMENT)
                .setUserId(comment.getUserId())
                .setEntityType(comment.getEntityType())
                .setEntityId(comment.getEntityId())
                .setData("postId", discussPostId);
        //回复的帖子或评论作者
        if(comment.getEntityType() == ENTITY_POST){
            DiscussPost discussPost = discussPostService.findDiscussPost(comment.getEntityId());
            event.setEntityUserId(discussPost.getUserId());
        }else if(comment.getEntityType() == ENTITY_COMMENT){
            Comment target = commentService.findCommentById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        }
        eventProducer.fireEvent(event);
        return "redirect:/discuss/detail/" + discussPostId;
    }

}
