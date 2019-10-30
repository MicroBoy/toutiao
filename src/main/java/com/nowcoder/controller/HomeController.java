package com.nowcoder.controller;

import com.nowcoder.model.EntityType;
import com.nowcoder.model.HostHolder;
import com.nowcoder.model.News;
import com.nowcoder.model.ViewObject;
import com.nowcoder.service.LikeService;
import com.nowcoder.service.NewsService;
import com.nowcoder.service.UserService;
import com.nowcoder.util.MailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nowcoder on 2016/7/2.
 *
 *  网站主页显示
 */
@Controller
@RequestMapping(path = "/toutiao")
public class HomeController {
    @Autowired
    NewsService newsService;

    @Autowired
    UserService userService;

    @Autowired
    LikeService likeService;

    @Autowired
    HostHolder hostHolder; //当前用户线程

    @Autowired
    MailSender mailSender;

    /**
     *  获取资讯
     * @param userId
     * @param offset
     * @param limit
     * @return
     */
    private List<ViewObject> getNews(int userId, int offset, int limit) {

        List<News> newsList = newsService.getLatestNews(userId, offset, limit);

        int localUserId = hostHolder.getUser() != null ? hostHolder.getUser().getId() : 0; //用户是否登录，hostHolder是在登陆拦截器中获取的

        List<ViewObject> vos = new ArrayList<>(); //ViewObject就是一个<String,Object>的map，记录用户处理一条资讯的所有信息

        for (News news : newsList) {
            ViewObject vo = new ViewObject();

            vo.set("news", news);
            vo.set("user", userService.getUser(news.getUserId()));

            //登录用户才会展示对该资讯的点/踩
            if (localUserId != 0) {
                vo.set("like", likeService.getLikeStatus(localUserId, EntityType.ENTITY_NEWS, news.getId()));
            } else {
                vo.set("like", 0);
            }
            vos.add(vo);
        }

        return vos;
    }

    /**
     *      展示所有用户最新的【资讯列表】
     * @param model
     * @param pop
     * @return
     */
    @RequestMapping(path = {"/index"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String index(Model model,
                        @RequestParam(value = "pop", defaultValue = "0",required = false) int pop) {

        model.addAttribute("vos", getNews(0, 0, 10));//NewsDAO.xml中配置了if(userId != 0)，才会精确查询，否则查询所有

        if (hostHolder.getUser() != null) {
            pop = 0;
        }
        model.addAttribute("pop", pop);

        return "home";
    }

    /**
     *      展示某用户userId下的【资讯列表】
     * @param model
     * @param userId
     * @return
     */
    @RequestMapping(path = {"/user/{userId}"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String userIndex(Model model, @PathVariable("userId") int userId) {
        model.addAttribute("vos", getNews(userId, 0, 10));
        return "home";
    }


}
