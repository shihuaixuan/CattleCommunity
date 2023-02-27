package com.twenty.cattlecommuntiy.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.twenty.cattlecommuntiy.entity.DiscussPost;
import com.twenty.cattlecommuntiy.service.DiscussPostService;
import com.twenty.cattlecommuntiy.mapper.DiscussPostMapper;
import com.twenty.cattlecommuntiy.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
* @author twenty
* @description 针对表【discuss_post】的数据库操作Service实现
* @createDate 2023-01-05 16:43:23
*/
@Service
public class DiscussPostServiceImpl extends ServiceImpl<DiscussPostMapper, DiscussPost>
    implements DiscussPostService{
    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Autowired
    private SensitiveFilter sensitiveFilter;
    @Override
    public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit) {
        return discussPostMapper.selectDiscussPosts(userId, offset, limit);
    }

    @Override
    public int findDiscussPostRow(int userId) {
        return discussPostMapper.selectDiscussPostRows(userId);
    }

    /**
     * 发布帖子
     * @param post
     * @return
     */
    @Override
    public int addDiscussPost(DiscussPost post) {
        if(post == null){
            throw new IllegalArgumentException("参数不能为空");
        }
        //专业html字符
        post.setTitle(HtmlUtils.htmlEscape(post.getTitle()));
        post.setContent(HtmlUtils.htmlEscape(post.getContent()));
        //过滤敏感词
        post.setTitle(sensitiveFilter.filter(post.getTitle()));
        post.setContent(sensitiveFilter.filter(post.getContent()));
        post.setStatus(0);
        post.setType(0);
        return discussPostMapper.insert(post);
    }

    /**
     * 查询一个帖子
     * @param disscussPostId
     * @return
     */
    @Override
    public DiscussPost findDiscussPost(int disscussPostId) {
        return discussPostMapper.selectById(disscussPostId);
    }

    /**
     * 跟新帖子的数量
     * @param id
     * @param commentCount
     * @return
     */
    @Override
    public int updateCommentCount(int id, int commentCount) {
        return discussPostMapper.updateCommentCount(id, commentCount);
    }


}




