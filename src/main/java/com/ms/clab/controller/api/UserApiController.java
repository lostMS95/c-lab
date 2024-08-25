package com.ms.clab.controller.api;

import com.ms.clab.service.SvnService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.tmatesoft.svn.core.SVNCommitInfo;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Controller
public class UserApiController {

    private final SvnService svnService;

    public UserApiController(SvnService svnService) {
        this.svnService = svnService;
    }

    @PostMapping("/svn/upload")
    public ModelAndView handleFileUpload(@RequestParam("title") String title,
                                         @RequestParam("description") String description,
                                         @RequestParam("position") String position,
                                         @RequestParam("expend_type") String expendType,
                                         @RequestParam("card_type") String cardType,
                                         @RequestParam("excel_date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate excelDate,
                                         @RequestParam("excel_detail") String excelDetail,
                                         @RequestParam("excel_amount") String excelAmount,
                                         @RequestParam("file") MultipartFile file) {
        ModelAndView modelAndView = new ModelAndView("home/home");

        try {
            // 1. 파일을 서버에 저장
            String filePath = svnService.saveFile(file);

            String formattedDate = excelDate.format(DateTimeFormatter.ofPattern("yyyy년 M월 d일 E요일", Locale.KOREAN));

            // 2. SVN에 파일 업로드
            File dest = new File(filePath);
            SVNCommitInfo commitInfo = svnService.uploadToSVN(dest, title, description);
            svnService.checkoutAndModifyExcel(position, expendType, cardType, formattedDate, excelDetail, excelAmount);

            modelAndView.addObject("message", "파일이 SVN에 성공적으로 업로드되었습니다: " + file.getOriginalFilename());
            modelAndView.addObject("commitInfo", commitInfo);
        } catch (Exception e) {
            modelAndView.addObject("message", "파일 업로드 실패: " + e.getMessage());
        }

        return modelAndView;
    }
}
