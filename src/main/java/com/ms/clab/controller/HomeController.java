package com.ms.clab.controller;

import com.ms.clab.service.FileUploadService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.tmatesoft.svn.core.SVNCommitInfo;

import java.io.File;

@Controller
public class HomeController {

    private final FileUploadService fileUploadService;

    public HomeController(FileUploadService fileUploadService) {
        this.fileUploadService = fileUploadService;
    }

    @GetMapping("/home")
    public String showHomePage() {
        return "home";
    }

    @PostMapping("/upload")
    public ModelAndView handleFileUpload(@RequestParam("file") MultipartFile file) {
        ModelAndView modelAndView = new ModelAndView("home");

        try {
            // 1. 파일을 서버에 저장
            String filePath = fileUploadService.saveFile(file);

            // 2. SVN에 파일 업로드
            File dest = new File(filePath);
            SVNCommitInfo commitInfo = fileUploadService.uploadToSVN(dest);

            modelAndView.addObject("message", "File uploaded successfully to SVN: " + file.getOriginalFilename());
            modelAndView.addObject("commitInfo", commitInfo);
        } catch (Exception e) {
            modelAndView.addObject("message", "File upload failed: " + e.getMessage());
        }

        return modelAndView;
    }
}
