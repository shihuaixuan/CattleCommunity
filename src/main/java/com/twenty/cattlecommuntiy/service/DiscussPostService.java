package com.twenty.cattlecommuntiy.service;

import com.twenty.cattlecommuntiy.entity.DiscussPost;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author twenty
* @description 针对表【discuss_post】的数据库操作Service
* @createDate 2023-01-05 16:43:23
*/
public interface DiscussPostService extends IService<DiscussPost> {
    /**
     * 查找分页
     * @param userId
     * @param offset
     * @param limit
     * @return
     */
    List<DiscussPost> findDiscussPosts(int userId, int offset, int limit);

    /**
     * 用户发帖数
     * @param userId
     * @return
     */
    int findDiscussPostRow(int userId);

    /**
     * 发布帖子
     * @param post
     * @return
     */
    int addDiscussPost(DiscussPost post);

    /**
     * 查询一个帖子
     * @param disscussPostId
     * @return
     */
    DiscussPost findDiscussPost(int disscussPostId);
    /**
     *跟新帖子的数量
     */
    int updateCommentCount(int id, int commentCount);
}
