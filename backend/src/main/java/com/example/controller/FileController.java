package com.example.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import com.example.common.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件接口
 */
@RestController
@RequestMapping("/files")
public class FileController {

    private static final Logger log = LoggerFactory.getLogger(FileController.class);

    // 文件存储的基础路径
    private static final String fileBasePath = System.getProperty("user.dir") + "/files/";

    /**
     * 文件上传
     */
    @PostMapping("/upload")
    public Result upload(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();

        // 1. 【核心修改】获取当前日期并生成 YYYY/MM/dd 格式的路径
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy" + File.separator + "MM" + File.separator + "dd");
        String datePath = today.format(formatter); // 生成例如 "2025/08/27" 的路径

        // 2. 【核心修改】创建包含日期路径的完整目录
        String fullDirectoryPath = fileBasePath + datePath;
        if (!FileUtil.isDirectory(fullDirectoryPath)) {
            FileUtil.mkdir(fullDirectoryPath);
        }

        // 3. 生成新的唯一文件名并拼接完整物理路径
        String newFileName = System.currentTimeMillis() + "-" + originalFilename;
        String realFilePath = fullDirectoryPath + File.separator + newFileName;

        try {
            // 4. 将文件写入目标路径
            FileUtil.writeBytes(file.getBytes(), realFilePath);
        } catch (Exception e) {
            log.error(originalFilename + "--文件上传失败", e);
            // 可以在这里返回一个错误结果
            // return Result.error("文件上传失败");
        }

        // 5. 【核心修改】返回包含日期路径的URL给前端
        // URL路径使用斜杠'/'，即使在Windows系统上
        String url = "/files/download/" + datePath.replace(File.separator, "/") + "/" + newFileName;
        return Result.success(url);
    }


    /**
     * 获取文件
     */
    @GetMapping("/download/**") // 【核心修改】使用 ** 来匹配多级路径，例如 /download/2025/08/27/file.jpg
    public void download(HttpServletRequest request, HttpServletResponse response) {
        // 从请求URL中动态提取文件路径
        String requestURI = request.getRequestURI();
        String filePathSuffix = requestURI.substring(requestURI.indexOf("/download/") + "/download/".length());

        OutputStream os;
        try {
            if (StrUtil.isNotEmpty(filePathSuffix)) {
                // 拼接文件的完整物理路径
                String realFilePath = fileBasePath + filePathSuffix;

                // 从路径中提取原始文件名，用于下载时显示
                String fileName = filePathSuffix.substring(filePathSuffix.lastIndexOf("/") + 1);
                
                // 根据文件扩展名设置正确的Content-Type
                String contentType = getContentType(fileName);
                response.setContentType(contentType);
                
                // 如果是图片文件，设置为内联显示而不是下载
                if (isImageFile(fileName)) {
                    response.addHeader("Content-Disposition", "inline;filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8));
                } else {
                    response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8));
                }
                
                byte[] bytes = FileUtil.readBytes(realFilePath);
                os = response.getOutputStream();
                os.write(bytes);
                os.flush();
                os.close();
            }
        } catch (Exception e) {
            log.warn("文件下载失败：" + filePathSuffix);
        }
    }
    
    /**
     * 根据文件名获取Content-Type
     */
    private String getContentType(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        switch (extension) {
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            case "bmp":
                return "image/bmp";
            case "webp":
                return "image/webp";
            case "svg":
                return "image/svg+xml";
            case "pdf":
                return "application/pdf";
            case "txt":
                return "text/plain";
            case "html":
                return "text/html";
            case "css":
                return "text/css";
            case "js":
                return "application/javascript";
            case "json":
                return "application/json";
            case "xml":
                return "application/xml";
            default:
                return "application/octet-stream";
        }
    }
    
    /**
     * 判断是否为图片文件
     */
    private boolean isImageFile(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        return extension.matches("jpg|jpeg|png|gif|bmp|webp|svg");
    }

    /**
     * wang-editor编辑器文件上传接口
     */
    @PostMapping("/wang/upload")
    public Map<String, Object> wangEditorUpload(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();

        // 同样，为wang-editor的上传也增加日期路径
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy" + File.separator + "MM" + File.separator + "dd");
        String datePath = today.format(formatter);

        String fullDirectoryPath = fileBasePath + datePath;
        if (!FileUtil.isDirectory(fullDirectoryPath)) {
            FileUtil.mkdir(fullDirectoryPath);
        }

        String newFileName = System.currentTimeMillis() + "-" + originalFilename;
        String realFilePath = fullDirectoryPath + File.separator + newFileName;

        try {
            FileUtil.writeBytes(file.getBytes(), realFilePath);
            System.out.println(originalFilename + "--上传成功");
        } catch (Exception e) {
            System.err.println(originalFilename + "--文件上传失败");
        }

        // 返回给编辑器的URL也必须是包含日期路径的完整URL
        String url = "/files/download/" + datePath.replace(File.separator, "/") + "/" + newFileName;

        Map<String, Object> resMap = new HashMap<>();
        resMap.put("errno", 0);
        resMap.put("data", CollUtil.newArrayList(Dict.create().set("url", url)));
        return resMap;
    }
}