package com.twenty.cattlecommuntiy.controller;

import com.alibaba.fastjson.JSONObject;
import com.twenty.cattlecommuntiy.entity.Message;
import com.twenty.cattlecommuntiy.entity.Page;
import com.twenty.cattlecommuntiy.entity.User;
import com.twenty.cattlecommuntiy.service.MessageService;
import com.twenty.cattlecommuntiy.service.UserService;
import com.twenty.cattlecommuntiy.util.ActivateConstant;
import com.twenty.cattlecommuntiy.util.CommunityUtil;
import com.twenty.cattlecommuntiy.util.HostHonder;
import jdk.nashorn.internal.runtime.regexp.joni.ast.StringNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import java.util.*;

@Controller
public class MessageController implements ActivateConstant {

    @Autowired
    private MessageService messageService;
    @Autowired
    private HostHonder hostHonder;
    @Autowired
    private UserService userService;

    /**
     * 私信列表
     * @param model
     * @param page
     * @return
     */
    @GetMapping("/letter/list")
    public String getLetterList(Model model, Page page){
         User user = hostHonder.getUser();
         //分页信息
        page.setLimit(5);
        page.setPath("/letter/list");
        page.setRows(messageService.findselectConversationCount(user.getId()));
        //会话列表
        List<Message> messages = messageService.findselectConversations(user.getId(), page.getOffset(), page.getLimit());
        List<Map<String, Object>> conversations = new ArrayList<>();
        if(messages != null){
            for(Message message : messages){
                Map<String , Object> map = new HashMap<>();
                map.put("conversation", message);
                map.put("letterCount", messageService.findselectLetterCount(message.getConversationId()));
                map.put("unReadCount", messageService.findselectLetterUnRead(user.getId(), message.getConversationId()));
                int targetId = user.getId() == message.getFromId() ? message.getToId() : message.getFromId();
                map.put("target", userService.findUserById(targetId));

                conversations.add(map);
            }
        }
        model.addAttribute("conversations", conversations);

        //查询全部未读消息数量
        int letterUnReadCount = messageService.findselectLetterUnRead(user.getId(), null);
        model.addAttribute("letterUnReadCount",letterUnReadCount );
        int noticeUnRead = messageService.findNotReadNotice(user.getId(), null);
        model.addAttribute("noticeUnRead", noticeUnRead);

        return "/site/letter";
    }

    /**
     * 私信详情
     */
    @GetMapping("/letter/detail/{conversationId}")
    public String getDetailLetter(@PathVariable("conversationId") String conversationId, Page page, Model model){
        page.setLimit(5);
        page.setPath("/letter/detail/"+conversationId);
        page.setRows(messageService.findselectLetterCount(conversationId));
        //私信列表
        List<Message> lettersList = messageService.findselectLetters(conversationId, page.getOffset(), page.getLimit());
        List<Map<String, Object>> letters = new ArrayList<>();
        if(lettersList != null){
            for(Message letter : lettersList){
                Map<String, Object> map = new HashMap<>();
                map.put("letter", letter);
                map.put("formUser", userService.findUserById(letter.getFromId()));
                letters.add(map);
            }
        }
        model.addAttribute("letters", letters);

        //私信目标
        model.addAttribute("target", getLetterTarget(conversationId));
        //设置已读
        List<Integer> ids = getLetterIds(lettersList);
        if(!ids.isEmpty()){
            messageService.readMessage(ids);
        }
        return "/site/letter-detail";
    }

    /**
     * 发送私信操作
     * @param toName
     * @param content
     * @return
     */
    @PostMapping("/letter/send")
    @ResponseBody
    public String sendLetter(String toName, String content){
        User target = userService.findUserByName(toName);
        if(target == null){
            return CommunityUtil.getJSONString("1","目标用户不存在");
        }
        Message message = new Message();
        message.setFromId(hostHonder.getUser().getId());
        message.setToId(target.getId());
        if(message.getFromId() < message.getToId()){
            message.setConversationId(message.getFromId() + "_" + message.getToId());
        }else {
            message.setConversationId(message.getToId() + "_" + message.getFromId());
        }
        message.setContent(content);
        message.setCreateTime(new Date());
        message.setStatus(0);
        messageService.addMessage(message);
        return CommunityUtil.getJSONString("0");
    }

    /**
     * 获取系统通知信息
     * @param model
     * @return
     */
    @GetMapping("/notice/list")
    public String getNotice(Model model){
        User user = hostHonder.getUser();

        //查询评论类通知
        Message message = messageService.findLastNotice(user.getId(), TOPIC_COMMENT);
        Map<String, Object> messageVO = new HashMap<>();
        if(message != null){
            messageVO.put("message", message);

            String content = HtmlUtils.htmlUnescape(message.getContent());
            HashMap<String, Object> data = JSONObject.parseObject(content, HashMap.class);
            messageVO.put("user",userService.findUserById((Integer) data.get("userId")) );
            messageVO.put("entityType", data.get("entityType"));
            messageVO.put("entityId", data.get("entityId"));
            messageVO.put("postId", data.get("postId"));

            int count = messageService.findNoticCount(user.getId(), TOPIC_COMMENT);
            messageVO.put("count", count);

            int unread = messageService.findNotReadNotice(user.getId(), TOPIC_COMMENT);
            System.out.println(unread);
            messageVO.put("unread", unread);
        }
        model.addAttribute("commentNotice", messageVO);
        //查询点赞类通知
        message = messageService.findLastNotice(user.getId(), TOPIC_LIKE);
        messageVO = new HashMap<>();
        if(message != null){
            messageVO.put("message", message);

            String content = HtmlUtils.htmlUnescape(message.getContent());
            HashMap<String, Object> data = JSONObject.parseObject(content, HashMap.class);
            messageVO.put("user",userService.findUserById((Integer) data.get("userId")) );
            messageVO.put("entityType", data.get("entityType"));
            messageVO.put("entityId", data.get("entityId"));
            messageVO.put("postId", data.get("postId"));

            int count = messageService.findNoticCount(user.getId(), TOPIC_LIKE);
            messageVO.put("count", count);

            int unread = messageService.findNotReadNotice(user.getId(), TOPIC_LIKE);
            messageVO.put("unread", unread);
        }
        model.addAttribute("likeNotice", messageVO);
        //查询关注类通知
        message = messageService.findLastNotice(user.getId(), TOPIC_FOLLOW);
        messageVO = new HashMap<>();
        if(message != null){
            messageVO.put("message", message);

            String content = HtmlUtils.htmlUnescape(message.getContent());
            HashMap<String, Object> data = JSONObject.parseObject(content, HashMap.class);
            messageVO.put("user",userService.findUserById((Integer) data.get("userId")) );
            messageVO.put("entityType", data.get("entityType"));
            messageVO.put("entityId", data.get("entityId"));

            int count = messageService.findNoticCount(user.getId(), TOPIC_FOLLOW);
            messageVO.put("count", count);

            int unread = messageService.findNotReadNotice(user.getId(), TOPIC_FOLLOW);
            messageVO.put("unread", unread);
        }
        model.addAttribute("followNotice", messageVO);
        //查询未读消息数量
        int letterUnRead = messageService.findselectLetterUnRead(user.getId(), null);
        int noticeUnRead = messageService.findNotReadNotice(user.getId(), null);
        model.addAttribute("letterUnRead", letterUnRead);
        model.addAttribute("noticeUnRead", noticeUnRead);

        return "/site/notice";
    }

    @GetMapping("/notice/detail/{topic}")
    public String getNoticeDetail(@PathVariable("topic")String topic, Page page, Model model){
        User user = hostHonder.getUser();

        page.setLimit(5);
        page.setPath("/notice/detail/"+topic);
        page.setRows(messageService.findNoticCount(user.getId(), topic));

        List<Message> notices = messageService.findNotices(user.getId(), topic, page.getOffset(), page.getLimit());
        List<Map<String, Object>> noticeVoList = new ArrayList<>();
        if(notices != null){
            for(Message notice : notices){
                Map<String, Object> map = new HashMap<>();
                map.put("notice", notice);

                String content = HtmlUtils.htmlUnescape(notice.getContent());
                HashMap<String, Object> data = JSONObject.parseObject(content, HashMap.class);
                map.put("user",userService.findUserById((Integer) data.get("userId")) );
                map.put("entityType", data.get("entityType"));
                map.put("entityId", data.get("entityId"));
                map.put("postId", data.get("postId"));
                //通知作者
                map.put("fromUser", userService.findUserById(notice.getFromId()));

                noticeVoList.add(map);
            }
        }
        model.addAttribute("notices",noticeVoList);
        //设置已读
        List<Integer> ids = getLetterIds(notices);
        if(!ids.isEmpty()){
            messageService.readMessage(ids);
        }
        return "/site/notice-detail";
    }

    private User getLetterTarget(String conversationId){
         String[] ids = conversationId.split("_");
         int id0 = Integer.parseInt(ids[0]);
         int id1 = Integer.parseInt(ids[1]);
         if(hostHonder.getUser().getId() == id0){
             return userService.findUserById(id1);
         }else {
             return userService.findUserById(id0);
         }
    }
    //未读消息id
    private List<Integer> getLetterIds(List<Message> letterList){
        List<Integer> ids = new ArrayList<>();

        if(letterList != null){
            for(Message message : letterList){
                if(hostHonder.getUser().getId() == message.getToId() && message.getStatus() == 0){
                    ids.add(message.getId());
                }
            }
        }
        return ids;
    }

}
