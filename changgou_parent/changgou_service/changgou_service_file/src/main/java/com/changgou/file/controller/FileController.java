package com.changgou.file.controller;

import com.changgou.common.entity.Result;
import com.changgou.common.entity.StatusCode;
import com.changgou.common.exception.ExceptionCast;
import com.changgou.common.model.response.file.FileCode;
import com.changgou.file.pojo.FastDFSFile;
import com.changgou.file.util.FastDFSClient;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * @Author:Administrator
 * @Date: 2019/12/19 9:32
 */
@RestController
@RequestMapping("/file")
public class FileController {

    @PostMapping("/upload")
    public Result uploadFile(MultipartFile file){
        try {
            // 获取文件全名
            String originalFilename = file.getOriginalFilename();
            // 判断文件是否存在
            if (StringUtils.isEmpty(originalFilename)){
                ExceptionCast.cast(FileCode.NO_SUCH_FILE);
            }
            // 获取文件内容
            byte[] content = file.getBytes();
            // 获取文件扩展名
            String ext = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
            // 封装FastDFS文件对象
            FastDFSFile fastDFSFile = new FastDFSFile(originalFilename,content,ext);
            // 上传并接收返回值
            String[] uploadInfo = FastDFSClient.upload(fastDFSFile);
            // 构建文件访问路径
            String url = FastDFSClient.getTrackerUrl()+uploadInfo[0]+ File.separator+uploadInfo[1];
            // 返回结果
            return new Result(true,StatusCode.OK,"文件上传成功",url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Result(false, StatusCode.ERROR,"文件上传失败");
    }
}
