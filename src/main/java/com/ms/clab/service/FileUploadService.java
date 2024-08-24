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
import com.ms.clab.util.AESUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;

@Service
public class FileUploadService {

    private final UserRepository userRepository;
    private final AESUtil aesUtil;

    @Autowired
    public FileUploadService(UserRepository userRepository, AESUtil aesUtil) {
        this.userRepository = userRepository;
        this.aesUtil = aesUtil;
    }

    /**
     * 현재 인증된 사용자의 정보를 기반으로 SVNClientManager를 생성합니다.
     * @return SVNClientManager
     */
    /**
     * 현재 인증된 사용자의 정보를 반환합니다.
     * @return User 객체
     */
    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            String username = authentication.getPrincipal().toString();

            // JPA를 사용하여 데이터베이스에서 사용자 정보를 가져옴
            return userRepository.findByUserId(username)
                    .orElseThrow(() -> new UsernameNotFoundException("해당 아이디의 사용자를 찾을 수 없습니다"));
        } else {
            throw new UsernameNotFoundException("인증된 사용자 정보를 찾을 수 없습니다.");
        }
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

        String uploadDirectory = System.getProperty("user.dir") + File.separator + "uploads";
        Path dirPath = Paths.get(uploadDirectory);

        if (Files.notExists(dirPath)) {
            Files.createDirectories(dirPath);
        }

        String filePath = uploadDirectory + File.separator + file.getOriginalFilename();
        Path destination = Paths.get(filePath);
        file.transferTo(destination.toFile());

        return filePath;
    }

    /**
     * 주어진 사용자의 정보를 기반으로 SVNClientManager를 생성합니다.
     * @param user 현재 인증된 사용자
     * @return SVNClientManager
     */
    private SVNClientManager createSVNClientManager(User user) {
        // 비밀번호를 가져옴
        String encryptedPassword  = user.getUserPw();

        // 비밀번호 복호화
        String decryptedPassword;
        try {
            decryptedPassword = AESUtil.decrypt(encryptedPassword );
        } catch (Exception e) {
            throw new RuntimeException("비밀번호 복호화에 실패했습니다.", e);
        }

        // SVNClientManager 초기화
        return SVNClientManager.newInstance(SVNWCUtil.createDefaultOptions(true),
                SVNWCUtil.createDefaultAuthenticationManager(user.getUserId(), decryptedPassword));
    }

    public SVNCommitInfo uploadToSVN(File file, String title, String description) throws Exception {
        User authenticatedUser = getAuthenticatedUser();
        SVNClientManager clientManager = createSVNClientManager(authenticatedUser);

        // 파일 확장자 추출
        String fileName = file.getName();
        String fileExtension = "";

        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            fileExtension = fileName.substring(lastDotIndex); // 확장자 포함
        }

        // title에 확장자 추가
        title = title + fileExtension;

        String svnUrl = generateSVNUrl(title);
        String commitMessage = (description == null || description.isEmpty())
                ? "c-lab에 의해 commit됨. " + title
                : description;

        SVNCommitClient commitClient = clientManager.getCommitClient();
        return commitClient.doImport(file, SVNURL.parseURIEncoded(svnUrl), commitMessage, null, true, true, SVNDepth.INFINITY);
    }

    public String generateSVNUrl(String title) throws Exception {
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
                + year + "년/개인지출영수증/" + quarterFolder + "/" + title;

        return svnUrl;
    }
}
