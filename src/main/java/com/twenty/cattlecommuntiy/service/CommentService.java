package com.twenty.cattlecommuntiy.service;

import com.twenty.cattlecommuntiy.entity.Comment;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author twenty
* @description 针对表【comment】的数据库操作Service
* @createDate 2023-01-10 13:16:24
*/
public interface CommentService extends IService<Comment> {
    /**
     * 查找评论
     * @param entityType
     * @param entityId
     * @param offset
     * @param limit
     * @return
     */
    List<Comment> findCommentByEntity(int entityType, int entityId, int offset, int limit);
    /**
     * 通过entityId查找评论
     */
    Comment findCommentById(int entityId);
    /**
     * 评论数
     * @param entityType
     * @param entityId
     * @return
     */
    int findCommentCount(int entityType, int entityId);

    /**
     *增加帖子
     * @param comment
     * @return
     */
    int addComment(Comment comment);
}
