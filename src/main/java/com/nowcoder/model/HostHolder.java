package com.nowcoder.model;

import org.springframework.stereotype.Component;

/**
 * Created by nowcoder on 2016/7/3.
 */
@Component
public class HostHolder {
    //线程本地变量，每条线程只能看到自己的
    private static ThreadLocal<User> users = new ThreadLocal<User>();

    public User getUser() {
        return users.get();
    }

    public void setUser(User user) {  //用户登陆拦截器里，会添加当前用户
        users.set(user);
    }

    public void clear() {
        users.remove();
    }
}
