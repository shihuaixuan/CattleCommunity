package com.twenty.cattlecommuntiy.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.twenty.cattlecommuntiy.entity.Comment;
import com.twenty.cattlecommuntiy.mapper.DiscussPostMapper;
import com.twenty.cattlecommuntiy.service.CommentService;
import com.twenty.cattlecommuntiy.mapper.CommentMapper;
import com.twenty.cattlecommuntiy.util.ActivateConstant;
import com.twenty.cattlecommuntiy.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
* @author twenty
* @description 针对表【comment】的数据库操作Service实现
* @createDate 2023-01-10 13:16:24
*/
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment>
    implements CommentService, ActivateConstant {
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private SensitiveFilter sensitiveFilter;
    @Autowired
    private DiscussPostMapper discussPostMapper;
    /**
     * 查找评论
     * @param entityType
     * @param entityId
     * @param offset
     * @param limit
     * @return
     */
    @Override
    public List<Comment> findCommentByEntity(int entityType, int entityId, int offset, int limit) {
        return commentMapper.selectCommentByEntity(entityType, entityId, offset, limit);
    }

    /**
     * 评论
     * @param entityId
     * @return
     */
    @Override
    public Comment findCommentById(int entityId) {
        return commentMapper.selectById(entityId);
    }

    /**
     * 评论数
     * @param entityType
     * @param entityId
     * @return
     */
    @Override
    public int findCommentCount(int entityType, int entityId) {
        return commentMapper.selectCountByEntity(entityType, entityId);
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED)
    public int addComment(Comment comment) {
        if(comment == null){
            throw new IllegalArgumentException("参数不能为空");
        }
        //增加评论
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveFilter.filter(comment.getContent()));

        int insert = commentMapper.insert(comment);
        //修改帖子评论数量
        if(comment.getEntityType() == ENTITY_POST){
            int count = commentMapper.selectCountByEntity(comment.getEntityType(), comment.getEntityId());
            discussPostMapper.updateCommentCount(comment.getEntityId(), count);
        }

        return insert;
    }
}




