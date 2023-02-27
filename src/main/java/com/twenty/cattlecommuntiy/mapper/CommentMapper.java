package com.twenty.cattlecommuntiy.mapper;

import com.twenty.cattlecommuntiy.entity.Comment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* @author twenty
* @description 针对表【comment】的数据库操作Mapper
* @createDate 2023-01-10 13:16:24
* @Entity com.twenty.cattlecommuntiy.entity.Comment
*/
@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
    /**
     * 按类型与用户id查找评论
     * @param entityType
     * @param entityId
     * @param offset
     * @param limit
     * @return
     */
    List<Comment> selectCommentByEntity(int entityType, int entityId, int offset, int limit);

    /**
     * 查找行数
     * @param entityType
     * @param entityId
     * @return
     */
    int selectCountByEntity(int entityType, int entityId);
}




