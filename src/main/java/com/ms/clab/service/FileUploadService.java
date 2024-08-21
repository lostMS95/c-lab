package com.ms.clab.service;

import com.ms.clab.entity.User;
import com.ms.clab.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNCommitClient;
import org.tmatesoft.svn.core.SVNCommitInfo;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

@Service
public class FileUploadService {

    private final UserRepository userRepository;
    private final SVNClientManager clientManager;

    @Autowired
    public FileUploadService(UserRepository userRepository) {
        this.userRepository = userRepository;

        // 현재 인증된 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = null;
        String password = null;

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            username = userDetails.getUsername();

            // JPA를 사용하여 데이터베이스에서 사용자 정보를 가져옴
            User user = userRepository.findByUserId(username)
                    .orElseThrow(() -> new UsernameNotFoundException("해당 아이디의 사용자를 찾을 수 없습니다"));

            // 비밀번호는 데이터베이스에서 가져옴
            password = user.getUserPw();
        }

        // SVNClientManager 초기화
        this.clientManager = SVNClientManager.newInstance(SVNWCUtil.createDefaultOptions(true),
                SVNWCUtil.createDefaultAuthenticationManager(username, password));
    }

    /**
     * 파일을 서버에 저장
     * @param file
     * @return
     * @throws IOException
     */
    public String saveFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("업로드할 파일이 선택되지 않았습니다.");
        }

        String uploadDirectory = System.getProperty("user.dir") + "/uploads";
        File dir = new File(uploadDirectory);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String filePath = uploadDirectory + "/" + file.getOriginalFilename();
        File dest = new File(filePath);
        file.transferTo(dest);

        return filePath;
    }

    public SVNCommitInfo uploadToSVN(File file) throws Exception {
        String svnUrl = generateSVNUrl(file.getName());
        String commitMessage = "c-lab에 의해 commit됨. " + file.getName() + " [by gwpark]";

        SVNCommitClient commitClient = clientManager.getCommitClient();
        return commitClient.doImport(file, SVNURL.parseURIEncoded(svnUrl), commitMessage, null, true, true, SVNDepth.INFINITY);
    }

    public String generateSVNUrl(String fileName) {
        // 현재 날짜 가져오기
        LocalDate currentDate = LocalDate.now();

        // 현재 년도 가져오기
        int year = currentDate.getYear();

        // 현재 월 가져오기
        int month = currentDate.getMonthValue();

        // 분기 계산
        String quarterFolder;
        if (month >= 1 && month <= 3) {
            quarterFolder = "2024년 1분기(01월-03월)";
        } else if (month >= 4 && month <= 6) {
            quarterFolder = "2024년 2분기(04월~06월)";
        } else if (month >= 7 && month <= 9) {
            quarterFolder = "2024년 3분기(07월~09월)";
        } else {
            quarterFolder = "2024년 4분기(10월~12월)";
        }

        // SVN 경로 생성
        String svnUrl = "http://222.122.47.196/svn/docs2/04. 지출내역/2. 개인경비사용내역/"
                + year + "년/개인지출영수증/" + quarterFolder + "/" + fileName;

        return svnUrl;
    }
}
