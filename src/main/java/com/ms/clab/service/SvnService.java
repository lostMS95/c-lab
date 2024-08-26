package com.ms.clab.service;

import com.ms.clab.entity.User;
import com.ms.clab.repository.UserRepository;
import com.ms.clab.util.SvnUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.tmatesoft.svn.core.SVNCommitInfo;
import com.ms.clab.util.AESUtil;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
public class SvnService {

    private final UserRepository userRepository;
    private final AESUtil aesUtil;
    private final SvnUtil svnUtil;
    private final FileService fileService;

    @Autowired
    public SvnService(UserRepository userRepository, AESUtil aesUtil, SvnUtil svnUtil, FileService fileService) {
        this.userRepository = userRepository;
        this.aesUtil = aesUtil;
        this.svnUtil = svnUtil;
        this.fileService = fileService;
    }

    /**
     * 현재 인증된 사용자의 정보를 반환합니다.
     * @return User 객체
     */
    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            String username = authentication.getPrincipal().toString();

            return userRepository.findByUserId(username).orElseThrow(() -> new UsernameNotFoundException("해당 아이디의 사용자를 찾을 수 없습니다"));
        } else {
            throw new UsernameNotFoundException("인증된 사용자 정보를 찾을 수 없습니다.");
        }
    }

    public SVNCommitInfo svnUpload(String title, String description, String position,
                                                  String expendType, String cardType, LocalDate excelDate,
                                                  String excelDetail, String excelAmount, MultipartFile file) throws Exception {
        // 1. 파일을 서버에 저장
        String filePath = fileService.saveFile(file);

        // 2. 인증된 사용자 정보 가져오기
        User authenticatedUser = getAuthenticatedUser();

        // 3. 파일의 확장자를 추출하여 제목에 추가
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String titleWithExtension = title + fileExtension;

        // 4. SVN URL 생성
        String svnUrl = svnUtil.generateSVNUrl(titleWithExtension);

        // 5. SVN에 파일 업로드
        SVNCommitInfo commitInfo = svnUtil.uploadFileToSVN(
                svnUtil.createSVNClientManager(authenticatedUser),
                new File(filePath),
                svnUrl,
                description
        );

        // 5. 엑셀 파일 체크아웃 및 수정
        String formattedDate = excelDate.format(DateTimeFormatter.ofPattern("yyyy년 M월 d일 E요일", Locale.KOREAN));
        svnUtil.checkoutAndModifyExcel(authenticatedUser, position, expendType, cardType,
                formattedDate, excelDetail, excelAmount);

        return commitInfo;
    }

}