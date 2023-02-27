package com.twenty.cattlecommuntiy.util;

public class RedisKeyUtil {

    private static final String SPILT = ":";
    private static final String PREFIX_ENTITY_LIKE = "like:entity";
    private static final String PREFIX_USER_LIKE = "like:user";
    private static final String PREFIX_FOLLOWEE = "followee";
    private static final String PREFIX_FOLLIWER = "follower";
    private static final String PREFIX_KAPTCHA = "kaptcha";
    private static final String PREFIX_TICKET = "ticket";
    private static final String PREFIX_USER = "user";

    public static String getEntityLikeKey(int entityType, int entityId){
        return PREFIX_ENTITY_LIKE + SPILT + entityType + SPILT + entityId;
    }

    public static String getUserLikeKey(int userId){
        return PREFIX_USER_LIKE + SPILT + userId;
    }
    //某个用户关注的实体
    public static String getFolloweeKey(int userId, int entityType){
        return PREFIX_FOLLOWEE + SPILT + userId + SPILT + entityType;
    }
    //某个用户拥有的粉丝
    public static String getFollowerKey(int entityType, int entityId){
        return PREFIX_FOLLIWER + SPILT + entityType + SPILT + entityId;
    }
    //验证码
    public static String getKaptchaKey(String owner){
        return PREFIX_KAPTCHA + SPILT + owner;
    }
    //登入凭证
    public static String getTicketKey(String ticket){
        return PREFIX_TICKET + SPILT + ticket;
    }
    //用户
    public static String getUserKey(int userId){
        return PREFIX_USER + SPILT + userId;
    }
}
