package com.twenty.cattlecommuntiy.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

public class CommunityUtil {
    /**
     * 生成随机的字符串
     * @return
     */
    public static String genetateUUID(){
        String string = UUID.randomUUID().toString().replace("-", "");
        return string;
    }

    /**
     * MD5密码加密
     * 讲key+一个随机字符 -> MD5加密后的字符
     * @param key
     * @return
     */
    public static String MD5(String key){
        if(StringUtils.isBlank(key)){
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }

    /**
     * 封装JSON信息
     * @param code
     * @param msg
     * @param map
     * @return
     */
    public static String getJSONString(String code, String msg, Map<String, Object> map){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", code);
        jsonObject.put("msg", msg);
        if(map != null){
            for(String key : map.keySet()){
                jsonObject.put(key, map.get(key));
            }
        }
        return jsonObject.toJSONString();
    }

    public static String getJSONString(String code, String msg){
        return getJSONString(code, msg ,null);
    }

    public static String getJSONString(String code){
        return getJSONString(code, null ,null);
    }

}
