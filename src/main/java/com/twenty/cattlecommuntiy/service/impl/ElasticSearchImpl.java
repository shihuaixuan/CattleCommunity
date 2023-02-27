package com.twenty.cattlecommuntiy.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import co.elastic.clients.util.ObjectBuilder;
import com.twenty.cattlecommuntiy.entity.DiscussPost;

import com.twenty.cattlecommuntiy.mapper.elasticsearch.elasticsearchReposity;
import com.twenty.cattlecommuntiy.service.ElasticSearchService;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class ElasticSearchImpl implements ElasticSearchService {

    @Autowired
    private elasticsearchReposity elasticsearchReposity;
    @Autowired
    private ElasticsearchClient elasticsearchClient;

    public void saveDiscucssPost(DiscussPost post){
        elasticsearchReposity.save(post);
    }

    public void deleteDiscussPost(int id){
        elasticsearchReposity.deleteById(id);
    }

    public Page<DiscussPost> searchDiscussPost(String keyword, int current, int limit){

        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery(keyword, "title","content"))
                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withPageable(PageRequest.of(0, 10))
                .withHighlightFields(
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>")
                ).build();
        CreateIndexRequest request = new CreateIndexRequest("testCreateIndex");


                

    }
}
