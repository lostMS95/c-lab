package com.ms.clab.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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

@Service
public class FileUploadService {

    private SVNClientManager clientManager;

    public FileUploadService() {
        // 현재 인증된 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = null;
        String password = null;

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            username = userDetails.getUsername();
            // 비밀번호는 보통 UserDetails에는 저장되지 않기 때문에, 인증 시 제공된 비밀번호를 사용해야 함
            // 여기서는 가정으로 비밀번호를 관리하는 방법이 필요
            password = (String) authentication.getCredentials();
        }

        // SVNClientManager 초기화
        this.clientManager = SVNClientManager.newInstance(SVNWCUtil.createDefaultOptions(true),
                SVNWCUtil.createDefaultAuthenticationManager(username, password));
    }

    public String saveFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("No file selected for upload.");
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
        String svnUrl = "http://222.122.47.196/svn/docs2/06. 부서별자료/4. 유지보수/01. 유지보수문서/DB아카이브 오류 및 이슈/" + file.getName();
        String commitMessage = "Importing " + file.getName() + " to SVN";

        SVNCommitClient commitClient = clientManager.getCommitClient();
        return commitClient.doImport(file, SVNURL.parseURIEncoded(svnUrl), commitMessage, null, true, true, SVNDepth.INFINITY);
    }
}
