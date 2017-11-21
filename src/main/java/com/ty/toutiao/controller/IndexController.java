package com.ty.toutiao.controller;

import com.ty.toutiao.model.User;
import com.ty.toutiao.service.ToutiaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

//@Controller
public class IndexController {

    @Autowired
    private ToutiaoService toutiaoService;

    @RequestMapping(path = {"/", "/index"})
    @ResponseBody
    public String index(HttpSession session){
        return "Hello" + session.getAttribute("msg" + toutiaoService.say());
    }

    @RequestMapping(value = {"/profile/{groupId}/{userId}"})
    @ResponseBody
    public String profile(@PathVariable("groupId") String groupId,
                          @PathVariable("userId") int userId,
                          @RequestParam(value = "type",defaultValue = "1") int type,
                          @RequestParam(value = "key",defaultValue = "str") String key){
        return String.format("{%s},{%d},{%d},{%s}",
                groupId,userId,type,key);
    }

    @RequestMapping("/example")
    public String templateExample(Model model){
        model.addAttribute("name", "Dear");
        List<String> colors = Arrays.asList(new String[]{"RED","BLUE"});
        model.addAttribute("colors", colors);
        Map<String, String> map = new HashMap<String, String>();
        for(int i=0;i<4;++i){
            map.put(String.valueOf(i), String.valueOf(i*i));
        }
        model.addAttribute("map", map);
        model.addAttribute("user", new User("John"));
        return "what";
    }

    @RequestMapping("/request")
    @ResponseBody
    public String request(HttpServletRequest request,
                          HttpServletResponse response,
                          HttpSession session){
        StringBuilder sb=new StringBuilder();
        Enumeration<String> headerNames = request.getHeaderNames();
        while(headerNames.hasMoreElements()){
            String name = headerNames.nextElement();
            sb.append(name + ":" + request.getHeader(name) + "<br>");
        }

        for(Cookie cookie:request.getCookies()){
            sb.append("Cookie:");
            sb.append(cookie.getName() + ":");
            sb.append(cookie.getValue() + "<br>");
        }

        sb.append("getMethod:" + request.getMethod() + "<br>");
        sb.append("getPathInfo:" + request.getPathInfo() + "<br>");
        sb.append("getQueryString:" + request.getQueryString() + "<br>");
        sb.append("getRequestURI:" + request.getRequestURI() + "<br>");

        return sb.toString();
    }

    @RequestMapping("/response")
    @ResponseBody
    public String request(@CookieValue(value = "webId", defaultValue = "id01") String webId,
                          @RequestParam(value = "key", defaultValue = "key") String key,
                          @RequestParam(value = "value", defaultValue = "value") String value,
                          HttpServletResponse response){
        response.addCookie(new Cookie(key, value));
        response.addHeader(key, value);
        return "WebId:" + webId;
    }

    @RequestMapping("/redirect/{code}")
    public RedirectView  redirect(@PathVariable("code") int code,
                                  HttpSession session){
        RedirectView redirectView = new RedirectView("/", true);
        if(code==301){
            redirectView.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
        }
        session.setAttribute("msg", "Jump from redirect");
        return redirectView;
        // return "redirect:/";
    }

    @RequestMapping("/admin")
    @ResponseBody
    public String admin(@RequestParam(value = "key", required = false) String key){
        if("admin".equals(key)){
            return "hello admin";
        }
        throw new IllegalArgumentException("Key 错误");
    }

    @ExceptionHandler
    @ResponseBody
    public String error(Exception e){
        return "error:" + e.getMessage();
    }

}
