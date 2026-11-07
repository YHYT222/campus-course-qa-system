package com.campus.qa.controller;

import com.campus.qa.dto.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 图片上传接口 —— 采用本地磁盘存储方案，不依赖任何第三方云存储（需求硬性约束 7）
 */
@RestController
@RequestMapping("/api/upload")
public class UploadController {

    /** 图片保存目录 */
    @Value("${campus.upload.dir}")
    private String uploadDir;

    /** 图片访问前缀 */
    @Value("${campus.upload.access-prefix}")
    private String accessPrefix;

    /** 上传问题配图：POST /api/upload/image */
    @PostMapping("/image")
    public Result<Map<String, String>> upload(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return Result.fail("请选择要上传的图片");
        }
        // 校验文件类型
        String original = file.getOriginalFilename();
        String suffix = "";
        if (original != null && original.lastIndexOf(".") > -1) {
            suffix = original.substring(original.lastIndexOf(".")).toLowerCase();
        }
        if (!".jpg".equals(suffix) && !".jpeg".equals(suffix)
                && !".png".equals(suffix) && !".gif".equals(suffix) && !".bmp".equals(suffix)) {
            return Result.fail("仅支持 jpg / jpeg / png / gif / bmp 格式的图片");
        }
        // 限制 5MB 以内
        if (file.getSize() > 5 * 1024 * 1024) {
            return Result.fail("图片大小不能超过 5MB");
        }
        try {
            // 按日期分目录保存，避免单目录文件过多
            String datePath = new SimpleDateFormat("yyyyMMdd").format(new Date());
            File dir = new File(uploadDir + datePath + "/");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String fileName = UUID.randomUUID().toString().replace("-", "") + suffix;
            File dest = new File(dir, fileName);
            file.transferTo(dest);
            // 返回可访问路径，供前端回显与提交
            Map<String, String> data = new HashMap<String, String>();
            data.put("url", accessPrefix + datePath + "/" + fileName);
            return Result.ok("上传成功", data);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("图片上传失败：" + e.getMessage());
        }
    }
}
