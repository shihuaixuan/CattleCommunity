package com.twenty.cattlecommuntiy.mapper.elasticsearch;

import com.twenty.cattlecommuntiy.entity.DiscussPost;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface elasticsearchReposity extends ElasticsearchRepository<DiscussPost, Integer> {
}
