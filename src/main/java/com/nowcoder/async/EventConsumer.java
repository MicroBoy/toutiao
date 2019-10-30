package com.nowcoder.async;

import com.alibaba.fastjson.JSON;
import com.nowcoder.model.Message;
import com.nowcoder.util.JedisAdapter;
import com.nowcoder.util.RedisKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nowcoder on 2016/7/16.
 *
 *     从Redis消息队列中拉取事件处理
 */
@Service
public class EventConsumer implements InitializingBean, ApplicationContextAware {
    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);

    private Map<EventType, List<EventHandler>> config = new HashMap<EventType, List<EventHandler>>();

    //Spring容器,生成Bean实例的工厂,并管理容器中的Bean;  一般会称BeanFactory为IoC容器，而称ApplicationContext为应用上下文
    private ApplicationContext applicationContext;

    @Autowired
    JedisAdapter jedisAdapter;

    @Override
    public void afterPropertiesSet() throws Exception {

        //获取IOC容器里的Bean：事件对象
        Map<String, EventHandler> beans = applicationContext.getBeansOfType(EventHandler.class);//spring容器启动后，

        if (beans != null) {
            for (Map.Entry<String, EventHandler> entry : beans.entrySet()) {

                List<EventType> eventTypes = entry.getValue().getSupportEventTypes();

                for (EventType type : eventTypes) {
                    if (!config.containsKey(type)) {
                        config.put(type, new ArrayList<EventHandler>());
                    }
                    config.get(type).add(entry.getValue());  //得到 {事件：处理方法} 的键值对
                }
            }
        }

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {

                    String key = RedisKeyUtil.getEventQueueKey();

                    List<String> events = jedisAdapter.brpop(0, key); //消费Redis消息队列

                    for (String message : events) {

                        //此线程只处理点赞事件
                        if (message.equals(key)) {
                            continue;
                        }

                        EventModel eventModel = JSON.parseObject(message, EventModel.class);
                        if (!config.containsKey(eventModel.getType())) {
                            logger.error("不能识别的事件");
                            continue;
                        }

                        //获取事件类型，做对应处理
                        for (EventHandler handler : config.get(eventModel.getType())) {
                            handler.doHandle(eventModel);
                        }
                    }
                }
            }
        });
        thread.start();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
