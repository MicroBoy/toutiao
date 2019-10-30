package com.nowcoder.async;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.util.JedisAdapter;
import com.nowcoder.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by nowcoder on 2016/7/16.
 *
 *    将事件推到Redis消息队列中
 */
@Service
public class EventProducer {
    @Autowired
    JedisAdapter jedisAdapter;

    public boolean fireEvent(EventModel model) {
        try {
            String json = JSONObject.toJSONString(model);  //事件类型
            String key = RedisKeyUtil.getEventQueueKey();  //这是一个事件(EVENT)

            jedisAdapter.lpush(key, json);
            return true;

        } catch (Exception e) {
            return false;
        }
    }
}
