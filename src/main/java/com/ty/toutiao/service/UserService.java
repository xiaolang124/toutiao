package com.ty.toutiao.service;

import com.ty.toutiao.dao.LoginTicketDAO;
import com.ty.toutiao.dao.UserDAO;
import com.ty.toutiao.model.LoginTicket;
import com.ty.toutiao.model.User;
import com.ty.toutiao.util.MyUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private LoginTicketDAO loginTicketDAO;

    public Map<String, Object> register(String username,String password){
        Map<String,Object> map = new HashMap<String, Object>();
        if(StringUtils.isBlank(username)){
            map.put("msgUsername", "用户名不能为空");
            return map;
        }
        if(StringUtils.isBlank(password)){
            map.put("msgPassword", "密码不能为空");
            return map;
        }

        User user = userDAO.selectByName(username);
        if(user != null){
            map.put("msgUsername","用户名已经被注册");
            return map;
        }

        user = new User();
        user.setName(username);
        user.setSalt(UUID.randomUUID().toString().substring(0, 5));
        String head = String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000));
        user.setHeadUrl(head);
        user.setPassword(MyUtil.MD5((password+user.getSalt())));
        userDAO.addUser(user);

        return map;
    }

    public Map<String,Object> login(String username,String password){
        Map<String,Object> map = new HashMap<String, Object>();
        if(StringUtils.isBlank(username)){
            map.put("msgUsername", "用户名不能为空");
            return map;
        }
        if(StringUtils.isBlank(password)){
            map.put("msgPassword", "密码不能为空");
            return map;
        }

        User user = userDAO.selectByName(username);
        if(user == null){
            map.put("msgUsername","用户名不存在");
            return map;
        }

        if(!MyUtil.MD5(password+user.getSalt()).equals(user.getPassword())){
            map.put("msgPassword","密码不正确");
            return map;
        }

        String ticket = addLoginTicket(user.getId());
        map.put("ticket", ticket);

        return map;
    }

    private String addLoginTicket(int userId){
        LoginTicket ticket = new LoginTicket();
        ticket.setUserId(userId);
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.YEAR , 1);
        ticket.setExpired(cal.getTime());
        ticket.setStatus(0);
        ticket.setTicket(UUID.randomUUID().toString().replace("-",""));
        loginTicketDAO.addTicket(ticket);
        return ticket.getTicket();
    }

    public void logout(String ticket){
        loginTicketDAO.updateStatus(ticket, 1);
    }

    public User getUser(int id){
        return userDAO.selectById(id);
    }

}
