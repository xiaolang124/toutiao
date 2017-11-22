package com.ty.toutiao.controller;

import com.ty.toutiao.model.HostHolder;
import com.ty.toutiao.model.News;
import com.ty.toutiao.service.NewsService;
import com.ty.toutiao.service.QiniuService;
import com.ty.toutiao.util.MyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

@Controller
public class NewsController {

    private static final Logger logger = LoggerFactory.getLogger(NewsController.class);

    @Autowired
    private NewsService newsService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    QiniuService qiniuService;

    @RequestMapping("/user/addNews/")
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
}
