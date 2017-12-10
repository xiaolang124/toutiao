package com.ty.toutiao.controller;

import com.ty.toutiao.model.*;
import com.ty.toutiao.service.CommentService;
import com.ty.toutiao.service.NewsService;
import com.ty.toutiao.service.QiniuService;
import com.ty.toutiao.service.UserService;
import com.ty.toutiao.util.MyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class NewsController {

    private static final Logger logger = LoggerFactory.getLogger(NewsController.class);

    @Autowired
    private NewsService newsService;

    @Autowired
    private UserService userService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    CommentService commentService;

    @Autowired
    QiniuService qiniuService;

    @RequestMapping("/user/addNews")
    @ResponseBody
    public String addNews(@RequestParam("image") String image,
                          @RequestParam("title") String title,
                          @RequestParam("link") String link){
        try{
            News news = new News();
            if(hostHolder.getUser()!=null){
                news.setUserId(hostHolder.getUser().getId());
            }else{
                news.setUserId(3);
            }
            news.setImage(image);
            news.setCreateDate(new Date());
            news.setTitle(title);
            news.setLink(link);
            newsService.addNews(news);
            return MyUtil.getJSONString(0);
        }catch (Exception e){
            logger.error("添加咨询错误"+e.getMessage());
            return MyUtil.getJSONString(1,"发布失败");
        }
    }

    @RequestMapping(value = "/uploadImage",method = {RequestMethod.POST})
    @ResponseBody
    public String uploadImage(@RequestParam("file")MultipartFile file){
        try{
            String fileUrl = newsService.saveImage(file);
//            String fileUrl = qiniuService.saveImage(file);
            if(fileUrl==null){
                return MyUtil.getJSONString(1, "上传图片失败");
            }
            return MyUtil.getJSONString(1, "上传成功");

        }catch (Exception e){
            logger.error("上传图片失败" + e.getMessage());
            return MyUtil.getJSONString(1, "上传图片失败");
        }
    }

    @RequestMapping(value = "/image",method = {RequestMethod.GET})
    @ResponseBody
    public void getImage(@RequestParam(value = "name", defaultValue = "0") String image,
                         HttpServletResponse response){
        try{
            if(image == "0"){
                return;
            }
            response.setContentType("image/jpeg");
            StreamUtils.copy(new FileInputStream(new File(
                    MyUtil.IMAGE_DIR + image)),
                    response.getOutputStream());
//            URL url = new URL("http://ozt4fw0cv.bkt.clouddn.com/aeeefd46f807487d824304707525110f.jpeg");
//            HttpURLConnection httpUrl = (HttpURLConnection) url.openConnection();
//            StreamUtils.copy(new BufferedInputStream(httpUrl.getInputStream()),
//                    response.getOutputStream());
            response.getOutputStream();
        }catch (Exception e){
            logger.error("读取图片错误"+e.getMessage());
        }
    }

    @RequestMapping("/news/{newsId}")
    public String newsDetail(@PathVariable("newsId") int newsId,
                             Model model){
        News news = newsService.getById(newsId);
        if(news!=null){
            List<Comment> comments = commentService.getCommentsByEntity(news.getId(),
                    EntityType.ENTITY_NEWS);
            List<ViewObject> commentVOs = new ArrayList<>();
            for(Comment comment:comments){
                ViewObject vo = new ViewObject();
                vo.set("comment", comment);
                vo.set("user", userService.getUser(comment.getUserId()));
                commentVOs.add(vo);
            }
            model.addAttribute("comments", commentVOs);
            model.addAttribute("len",comments.size());
        }

        model.addAttribute("news", news);
        model.addAttribute("owner", userService.getUser(news.getUserId()));
        return "detail";
    }

    @RequestMapping(path = {"/addComment"}, method = {RequestMethod.POST})
    public String addComment(@RequestParam("newsId") int newsId,
                             @RequestParam("content") String content) {
        try {
            content = HtmlUtils.htmlEscape(content);
            // 过滤content
            Comment comment = new Comment();
            comment.setUserId(hostHolder.getUser().getId());
            comment.setContent(content);
            comment.setEntityId(newsId);
            comment.setEntityType(EntityType.ENTITY_NEWS);
            comment.setCreateDate(new Date());
            comment.setStatus(0);

            commentService.addComment(comment);
            // 更新news里的评论数量
            int count = commentService.getCommentCount(comment.getEntityId(), comment.getEntityType());
            newsService.updateCommentCount(comment.getEntityId(), count);
            // 怎么异步化
        } catch (Exception e) {
            logger.error("增加评论失败" + e.getMessage());
        }
        return "redirect:/news/" + String.valueOf(newsId);
    }


}
