package com.twenty.cattlecommuntiy.service;

import com.twenty.cattlecommuntiy.entity.DiscussPost;
import org.springframework.data.domain.Page;

public interface ElasticSearchService {
    void saveDiscucssPost(DiscussPost post);
    void deleteDiscussPost(int id);
    Page<DiscussPost> searchDiscussPost(String keyword, int current, int limit);
}
