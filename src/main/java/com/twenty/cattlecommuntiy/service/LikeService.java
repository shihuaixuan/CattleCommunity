package com.twenty.cattlecommuntiy.service;

public interface LikeService {
    public void like(int userId, int entityType, int entityId,int entityUserId);
    public long findEntityLikeCount(int entityType, int entityId);
    public int findEntityLikeStatus(int userId, int entityType, int entityId);
    public int findUserLikeCount(int userId);
}
