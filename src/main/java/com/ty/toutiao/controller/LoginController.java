package com.ty.toutiao.controller;

import com.sun.deploy.net.HttpResponse;
import com.ty.toutiao.service.UserService;
import com.ty.toutiao.util.MyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Controller
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;

    @RequestMapping("/register/")
    @ResponseBody
    public String register(Model model,
                      @RequestParam("userName") String userName,
                      @RequestParam("password") String password,
                      @RequestParam(value = "remember", defaultValue = "0") int remember){
        try{
            Map<String, Object> map = userService.register(userName,password);
            if(map.isEmpty()){
                return MyUtil.getJSONString(0, "注册成功");
            }else{
                return MyUtil.getJSONString(1, map);
            }
        }catch (Exception e){
            logger.error("注册异常" + e.getMessage());
            return MyUtil.getJSONString(2, "注册异常");
        }
    }

    @RequestMapping("/login/")
    @ResponseBody
    public String login(Model model,
                        @RequestParam("userName") String userName,
                        @RequestParam("password") String password,
                        @RequestParam(value = "remember", defaultValue = "0") int remember,
                        HttpServletResponse response){
        try{
            Map<String, Object> map = userService.login(userName,password);
            if(map.containsKey("ticket")){
                Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
                cookie.setPath("/");
                if(remember>0){
                    cookie.setMaxAge(3600*24*180);
                }
                response.addCookie(cookie);
                return MyUtil.getJSONString(0, "登陆成功");
            }else{
                return MyUtil.getJSONString(1, map);
            }
        }catch (Exception e){
            logger.error("登陆异常" + e.getMessage());
            return MyUtil.getJSONString(2, "登陆异常");
        }
    }
}
