package com.twenty.cattlecommuntiy.controller;


import com.twenty.cattlecommuntiy.entity.DiscussPost;
import com.twenty.cattlecommuntiy.entity.Page;
import com.twenty.cattlecommuntiy.entity.User;
import com.twenty.cattlecommuntiy.mapper.DiscussPostMapper;
import com.twenty.cattlecommuntiy.service.DiscussPostService;
import com.twenty.cattlecommuntiy.service.LikeService;
import com.twenty.cattlecommuntiy.service.UserService;
import com.twenty.cattlecommuntiy.util.ActivateConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController implements ActivateConstant {
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private UserService userService;
    @Autowired
    private LikeService likeService;

    /**
     * 论坛主页信息的获取封装
     * @param model
     * @return
     */
    @RequestMapping(value = "/index")
    public String getIndexPage(Model model, Page page){
        page.setRows(discussPostService.findDiscussPostRow(0));
        page.setPath("/index");
        //获取论坛文章信息
        List<DiscussPost> discussPosts = discussPostService.findDiscussPosts(0, page.getOffset(), page.getLimit());
        //拼接信息(帖子+用户)
        List<Map<String,Object>> mapList = new ArrayList<>();
        if(discussPosts!=null){
            for(DiscussPost discussPost : discussPosts){
                Map<String,Object> map = new HashMap<>();
                //通过帖子的userid获取用户信息
                User user = userService.findUserById(discussPost.getUserId());
                map.put("post",discussPost);
                map.put("user",user);
                long likeCount = likeService.findEntityLikeCount(ENTITY_POST, discussPost.getId());
                map.put("likeCount", likeCount);
                mapList.add(map);
            }
        }
        model.addAttribute("discussPosts", mapList);
        return "/index";
    }

}
