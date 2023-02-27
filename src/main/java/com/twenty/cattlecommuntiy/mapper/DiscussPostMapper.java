package com.twenty.cattlecommuntiy.mapper;

import com.twenty.cattlecommuntiy.entity.DiscussPost;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author twenty
* @description 针对表【discuss_post】的数据库操作Mapper
* @createDate 2023-01-05 16:43:23
* @Entity com.twenty.cattlecommuntiy.entity.DiscussPost
*/
@Mapper
public interface DiscussPostMapper extends BaseMapper<DiscussPost> {
    /**
     * 帖子分页
     * @param userId 查找一个用户发的帖子
     * @param offset 第几页
     * @param limit  一页多少帖子
     * @return
     */
    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit);

    /**
     * 一个用户发的帖子数
     * @param userId
     * @return
     */
    int selectDiscussPostRows(@Param("userId") int userId);

    /**
     * 更新帖子的数量
     */
    int updateCommentCount(int id, int commentCount);

}




