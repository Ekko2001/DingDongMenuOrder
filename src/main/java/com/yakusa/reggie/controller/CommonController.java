package com.yakusa.reggie.controller;
/*
    照片的上传和下载
 */

import com.yakusa.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {
    @Value("${reggie.path}")
    private String basePath;

    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){
        //file是一个临时文件,需要将文件保存到服务器的某个位置,然后将文件的路径返回给前端
        log.info("上传文件：{}",file.toString());
        //原始文件名
        String originalFilename = file.getOriginalFilename();//获取文件名
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

        //使用UUID生成一个随机的字符串,然后拼接后缀
        String fileName = UUID.randomUUID().toString()+suffix;//生成一个随机的字符串

        //将文件保存到服务器的某个位置
        File dir = new File(basePath);
        //判断文件夹是否存在,如果不存在,则创建
        if(!dir.exists()){
            dir.mkdirs();
        }


        try {
            file.transferTo(new File(basePath+fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return R.success(fileName);
    }



@GetMapping("/download")
    public void download(String name ,HttpServletResponse response){

    try {
        //输入流,通过输入流读取文件内容
        FileInputStream fileInputStream = new FileInputStream(new File(basePath+name));
        //输出流,通过输出流将文件内容写到浏览器
        ServletOutputStream outputStream = response.getOutputStream();

        response.setContentType("image/jpeg");//设置响应的内容类型


        int len = 0;
        byte[] buffer = new byte[1024];
        while((len=fileInputStream.read(buffer))!=-1){
            outputStream.write(buffer,0,len);
            outputStream.flush();
        }
        //关闭流
        outputStream.close();
        fileInputStream.close();

    } catch (Exception e) {
        e.printStackTrace();
    }



}






}
