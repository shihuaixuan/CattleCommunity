package com.twenty.cattlecommuntiy.util;

public interface ActivateConstant {
    /**
     * 激活成功
     */
    int ACTIVATE_SUCCESS = 0;
    /**
     * 重复激活
     */
    int ACTIVATE_REPLAY = 1;
    /**
     * 激活失败
     */
    int ACTIVATE_FAILURE = 2;
    /**
     * 默认状态的登入凭证超时时间
     */
    int DEFAULT_EXPIRED_SECONDS = 3600 * 12;
    /**
     * 记住状态的时间
     */
    int REMEMBER_EXPIRED_SECONDS = 3600 * 24 * 30;
    /**
     * 实体对象：帖子
     */
    int ENTITY_POST = 1;
    /**
     * 实体对象：评论
     */
    int ENTITY_COMMENT = 2;
    /**
     * 实体对象：用户
     */
    int ENTITY_USER = 3;
    /**
     * 主题：评论
     */
    String TOPIC_COMMENT = "comment";
    /**
     * 主题：点赞
     */
    String TOPIC_LIKE = "like";
    /**
     * 主题：关注
     */
    String TOPIC_FOLLOW = "follow";
    /**
     * 系统ID
     */
    int SYSTEN_USER_ID = 1;
}
