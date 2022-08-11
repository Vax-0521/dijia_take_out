package com.xyy.dijia.controller;


import com.xyy.dijia.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

/**
 * 文件上传和下载
 *
 */
@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {

    @Value("${dijia.path}")
    private String basePath;

    /**
     * 文件上传方法
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){
        //file是临时文件，需要转存到指定位置，否则本次请求完成后临时文件会删除
        log.info(file.toString());

        //获得原始文件名
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));//截取.jpg
        //使用UUID重新生成文件名字，防止文件名称覆盖
        String fileName = UUID.randomUUID().toString()+suffix; //动态拼接UUID加.jpg

        //创建一个目录对象
        File dir = new File(basePath);
        //判断当前目录是否存在
        if(!dir.exists()){
            //不存在就创建一个
            dir.mkdirs();
        }

        try {
            //将临时文件转存到指定位置，调用transferTo方法
            file.transferTo(new File(basePath+fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return R.success(fileName);
    }

    /**
     * 文件下载
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){
        //输入流，读取文件内容
        try {
            FileInputStream fis = new FileInputStream(basePath+name);
            //输出流，将文件写会浏览器，在浏览器展示图片了
            ServletOutputStream outputStream = response.getOutputStream();

            response.setContentType("image/jpeg"); //设置响应回去的文件类型

            byte[] bytes = new byte[1024];
            int len = 0;
            while ((len = fis.read(bytes)) != -1){
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }
            fis.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
