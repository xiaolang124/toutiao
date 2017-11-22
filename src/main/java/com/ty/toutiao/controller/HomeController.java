package com.ty.toutiao.controller;

import com.ty.toutiao.model.News;
import com.ty.toutiao.model.ViewObject;
import com.ty.toutiao.service.NewsService;
import com.ty.toutiao.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {
    @Autowired
    NewsService newsService;

    @Autowired
    UserService userService;

    private List<ViewObject> getNews(int userId, int offset, int limit){
        List<News> newsList = newsService.getLatestNews(userId, offset ,limit);
        List<ViewObject> vos = new ArrayList<>();
        for(News news : newsList){
            ViewObject vo = new ViewObject();
            vo.set("news",  news);
            vo.set("user", userService.getUser(news.getUserId()));
            vos.add(vo);
        }
        return vos;
    }

    @RequestMapping(path = {"/"})
    public String index(Model model){

        model.addAttribute("vos", getNews(0,0,10));
        return "home";
    }

    @RequestMapping(path = {"/user/{userId}"})
    public String userIndex(@PathVariable("userId") int userId,
                            Model model,
                            @RequestParam(value = "needLogin", defaultValue = "0") int needLogin){
        model.addAttribute("vos", getNews(userId,0,10));
        model.addAttribute("needLogin", needLogin);
        return "home";
    }
}
