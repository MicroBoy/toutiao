package com.nowcoder.async;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by nowcoder on 2016/7/16.
 *
 *      触发时间模板——登录事件、点赞事件
 */
public class EventModel {

    private EventType type;    //事件类型——点赞、登录、登录、评论
    private int actorId;       //事件触发者

    private int entityType;    //对象类型——资讯、别人的评论
    private int entityId;      //对象在数据库中的id
    private int entityOwnerId; //对象的所有者
    private Map<String, String> exts = new HashMap<String, String>();

    public String getExt(String key) {
        return exts.get(key);
    }
    public EventModel setExt(String key, String value) {
        exts.put(key, value);
        return this;
    }

    public EventModel(EventType type) {
        this.type = type;
    }
    public EventModel(){
    }

    public EventType getType() {
        return type;
    }

    public EventModel setType(EventType type) {
        this.type = type;
        return this;
    }

    public int getActorId() {
        return actorId;
    }

    public EventModel setActorId(int actorId) {
        this.actorId = actorId;
        return this;
    }

    public int getEntityType() {
        return entityType;
    }

    public EventModel setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public int getEntityId() {
        return entityId;
    }

    public EventModel setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public int getEntityOwnerId() {
        return entityOwnerId;
    }

    public EventModel setEntityOwnerId(int entityOwnerId) {
        this.entityOwnerId = entityOwnerId;
        return this;
    }

    public Map<String, String> getExts() {
        return exts;
    }

    public void setExts(Map<String, String> exts) {
        this.exts = exts;
    }
}
