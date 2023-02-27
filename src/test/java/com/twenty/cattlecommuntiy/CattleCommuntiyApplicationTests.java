package com.twenty.cattlecommuntiy;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import com.twenty.cattlecommuntiy.entity.DiscussPost;
import com.twenty.cattlecommuntiy.entity.User;
import com.twenty.cattlecommuntiy.mapper.DiscussPostMapper;
import com.twenty.cattlecommuntiy.mapper.UserMapper;
import com.twenty.cattlecommuntiy.util.MailClient;
import lombok.val;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.util.List;

import static org.elasticsearch.client.RequestOptions.*;

@SpringBootTest
class CattleCommuntiyApplicationTests {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Autowired
    private MailClient mailClient;
    @Autowired
    private TemplateEngine templateEngine;
    @Autowired
    private ElasticsearchClient elasticsearchClient;
    @Test
    void contextLoads() {
    }

    @Test
    public void test01(){
        User user = userMapper.selectById(101);
        User user1 = userMapper.selectByEmail("nowcoder111@sina.com");
        System.out.println(user1);
    }

    @Test
    public void testDiscussPost(){
        List<DiscussPost> discussPosts = discussPostMapper.selectDiscussPosts(0, 0, 10);
        for (DiscussPost discussPost : discussPosts){
            System.out.println(discussPost);
        }
        int i = discussPostMapper.selectDiscussPostRows(151);
        System.out.println(i);
    }
    @Test
    public void testSendMail(){
        mailClient.sendMail("shxtwenty@163.com","Test","Hello TWENTY");
    }
    @Test
    public void TestSendHtml(){
        Context context = new Context();
        context.setVariable("username","twenty");

        String process = templateEngine.process("/mail/demo", context);
        System.out.println(process);
        mailClient.sendMail("shxtwenty@163.com","HTML",process);

    }

    @Test
    public void TestElasticSearch() throws IOException {
        CreateIndexRequest request = new CreateIndexRequest("testCreateIndex");
        CreateIndexResponse response = elasticsearchClient.indices().create(request,
                DEFAULT);
        System.out.println(response);
    }

}
