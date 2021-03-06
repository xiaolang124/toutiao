package com.ty.toutiao;

import com.ty.toutiao.dao.CommentDAO;
import com.ty.toutiao.dao.LoginTicketDAO;
import com.ty.toutiao.dao.NewsDAO;
import com.ty.toutiao.dao.UserDAO;
import com.ty.toutiao.model.*;
import com.ty.toutiao.util.JedisAdapter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.Random;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ToutiaoApplication.class)
public class JedisTests {

    @Autowired
    JedisAdapter jedisAdapter;

    @Test
    public void jedisTest(){
        User user = new User();
        user.setHeadUrl("http://image.nowcoder.com/head/100t.png");
        user.setName("user1");
        user.setPassword("pwd");;
        user.setSalt("salt");

        jedisAdapter.setObject("user1xx", user);

        User u = jedisAdapter.getObject("user1xx", User.class);

        System.out.println(ToStringBuilder.reflectionToString(u));
    }
}
