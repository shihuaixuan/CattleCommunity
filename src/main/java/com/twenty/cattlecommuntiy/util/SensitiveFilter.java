package com.twenty.cattlecommuntiy.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveFilter {

    private static Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

    private static final String SENSITIVE = "***";

    //根节点
    TrieNode root = new TrieNode();
    @PostConstruct
    public void init(){
        try(
                InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("sensitive-words");
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                ){
            String keyword;
            while ((keyword = bufferedReader.readLine())!=null){
                this.addKeyWord(keyword);
            }
        }catch (IOException e){
            logger.error("读取关键词失败"+e.getMessage());
        }
    }

    /**
     * 添加敏感词
     * @param keyword
     */
    private void addKeyWord(String keyword){
        TrieNode TemptleNode = root;

        for(int i = 0; i<keyword.length(); i++){
            Character c = keyword.charAt(i);
            //判断是否有字符为c的子节点
            TrieNode subNode = TemptleNode.getSubNode(c);
            if(subNode == null){
                //初始化子节点
                 subNode = new TrieNode();
                 TemptleNode.setSubNodes(c, subNode);
            }
            //指向子节点
            TemptleNode = subNode;
            //判断是否结束
            if(i == keyword.length()-1){
                TemptleNode.setKeyWordEnd(true);
            }
        }
    }

    /**
     * 过滤敏感词
     * @param text
     * @return
     */
    public String filter(String text){
        if(StringUtils.isBlank(text)){
            return null;
        }
        // 指针1
        TrieNode tempNode = root;
        // 指针2
        int begin = 0;
        // 指针3
        int position = 0;
        // 结果
        StringBuilder sb = new StringBuilder();

        while(begin < text.length()){
            if(position < text.length()) {
                Character c = text.charAt(position);

                // 跳过符号
                if (isChar(c)) {
                    if (tempNode == root) {
                        begin++;
                        sb.append(c);
                    }
                    position++;
                    continue;
                }

                // 检查下级节点
                tempNode = tempNode.getSubNode(c);
                if (tempNode == null) {
                    // 以begin开头的字符串不是敏感词
                    sb.append(text.charAt(begin));
                    // 进入下一个位置
                    position = ++begin;
                    // 重新指向根节点
                    tempNode = root;
                }
                // 发现敏感词
                else if (tempNode.isKeyWordEnd()) {
                    sb.append(SENSITIVE);
                    begin = ++position;
                    tempNode = root;
                }
                // 检查下一个字符
                else {
                    position++;
                }
            }
            // position遍历越界仍未匹配到敏感词
            else{
                sb.append(text.charAt(begin));
                position = ++begin;
                tempNode = root;
            }
        }
        return sb.toString();
    }

    private boolean isChar(Character c){
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c>0x9FFF);
    }


    //前缀书
    private class TrieNode{

        //关键词结束标识
        private boolean isKeyWordEnd = false;
        //子节点
        Map<Character, TrieNode> subNodes = new HashMap<>();

        public boolean isKeyWordEnd() {
            return isKeyWordEnd;
        }

        public void setKeyWordEnd(boolean keyWordEnd) {
            isKeyWordEnd = keyWordEnd;
        }
        //添加子节点
        public void setSubNodes(Character c,TrieNode trieNode){
            subNodes.put(c,trieNode);
        }
        //获得子节点
        public TrieNode getSubNode(Character c){
            return subNodes.get(c);
        }
    }
}
