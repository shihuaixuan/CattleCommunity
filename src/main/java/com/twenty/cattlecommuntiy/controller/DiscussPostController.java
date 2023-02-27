package com.twenty.cattlecommuntiy.controller;

import com.twenty.cattlecommuntiy.entity.Comment;
import com.twenty.cattlecommuntiy.entity.DiscussPost;
import com.twenty.cattlecommuntiy.entity.Page;
import com.twenty.cattlecommuntiy.entity.User;
import com.twenty.cattlecommuntiy.service.CommentService;
import com.twenty.cattlecommuntiy.service.DiscussPostService;
import com.twenty.cattlecommuntiy.service.LikeService;
import com.twenty.cattlecommuntiy.service.UserService;
import com.twenty.cattlecommuntiy.util.ActivateConstant;
import com.twenty.cattlecommuntiy.util.CommunityUtil;
import com.twenty.cattlecommuntiy.util.HostHonder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements ActivateConstant {
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private HostHonder hostHonder;
    @Autowired
    private UserService userService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private LikeService likeService;

    /**
     * 发布帖子
     * @param title
     * @param context
     * @return
     */
    @PostMapping("/add")
    @ResponseBody
    public String addDiscussPost(String title, String context){
        User user = hostHonder.getUser();
        if(user == null){
            return CommunityUtil.getJSONString("403","请先登入！");
        }
        DiscussPost discussPost = new DiscussPost();
        discussPost.setUserId(user.getId());
        discussPost.setTitle(title);
        discussPost.setContent(context);
        discussPost.setCreateTime(new Date());
        discussPostService.addDiscussPost(discussPost);

        return CommunityUtil.getJSONString("0","发布成功");
    }

    /**
     * 传递处理过帖子的信息
     * @param discussPostId
     * @param model
     * @param page
     * @return
     */
    @GetMapping("/detail/{discussPostId}")
    public String getDisscussPost(@PathVariable("discussPostId") int discussPostId, Model model, Page page){
         DiscussPost post = discussPostService.findDiscussPost(discussPostId);
         model.addAttribute("post", post);
         User user = userService.findUserById(post.getUserId());
         model.addAttribute("user",user);
         //点赞数量
        long likeCount = likeService.findEntityLikeCount(ENTITY_POST, discussPostId);
        model.addAttribute("likeCount",likeCount);
        //点赞状态
        int likeStatus = hostHonder.getUser() == null ? 0 :
                likeService.findEntityLikeStatus(hostHonder.getUser().getId(), ENTITY_POST, discussPostId);
        model.addAttribute("likeStatus",likeStatus);
         //评论分页信息
        page.setLimit(5);
        page.setPath("/discuss/detail/" + discussPostId);
        page.setRows(post.getCommentCount());

        //评论：帖子的评论
        //恢复：评论的评论
        //评论列表
        List<Comment> commentList = commentService.findCommentByEntity(ENTITY_POST, post.getId(), page.getOffset(), page.getLimit());
        //评论展示列表
        List<Map<String, Object>> commentVoList = new ArrayList<>();
        if(commentList != null){
            for(Comment comment : commentList){
                //一个评论OV
                Map<String, Object> map = new HashMap<>();
                //评论与作者
                map.put("comment", comment);
                map.put("user", userService.findUserById(comment.getUserId()));
                //点赞数量
                likeCount = likeService.findEntityLikeCount(ENTITY_COMMENT, comment.getId());
                map.put("likeCount",likeCount);
                //点赞状态
                likeStatus = hostHonder.getUser() == null ? 0 :
                        likeService.findEntityLikeStatus(hostHonder.getUser().getId(), ENTITY_COMMENT, comment.getId());
                map.put("likeStatus", likeStatus);
                //回复列表
                List<Comment> replayList = commentService.findCommentByEntity(ENTITY_COMMENT, comment.getId(), 0, Integer.MAX_VALUE);
                List<Map<String, Object>> replayVoList = new ArrayList<>();
                if(replayList != null) {
                    for (Comment replay : replayList) {
                        Map<String, Object> replayOv = new HashMap<>();
                        replayOv.put("replay", replay);
                        replayOv.put("user", userService.findUserById(replay.getUserId()));
                        //回复目标
                        //出现一个空指针bug -> 在不需要指定回复人时，未在前端传入targetId的值，使得其在从数据库中取值时未null
                        User target = replay.getTargetId() == 0 ? null : userService.findUserById(replay.getUserId());
                        replayOv.put("target", target);
                        //点赞数量
                        likeCount = likeService.findEntityLikeCount(ENTITY_COMMENT, replay.getId());
                        replayOv.put("likeCount",likeCount);
                        //点赞状态
                        likeStatus = hostHonder.getUser() == null ? 0 :
                                likeService.findEntityLikeStatus(hostHonder.getUser().getId(), ENTITY_COMMENT, replay.getId());
                        replayOv.put("likeStatus", likeStatus);
                        replayVoList.add(replayOv);
                    }
                }
                map.put("replays", replayVoList);
                //回复数量
                int replayCount = commentService.findCommentCount(ENTITY_COMMENT, comment.getId());
                map.put("replayCount", replayCount);

                commentVoList.add(map);
            }
        }
        model.addAttribute("comments", commentVoList);
        return "/site/discuss-detail";
    }
}
