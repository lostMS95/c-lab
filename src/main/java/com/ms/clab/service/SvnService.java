package com.ms.clab.service;

import com.ms.clab.entity.User;
import com.ms.clab.repository.UserRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.*;
import org.tmatesoft.svn.core.SVNCommitInfo;
import com.ms.clab.util.AESUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class SvnService {

    private final UserRepository userRepository;
    private final AESUtil aesUtil;

    private static final String svnURL = "http://222.122.47.196/svn/docs2/04. 지출내역/2. 개인경비사용내역/2024년";
    private static final String localURL = System.getProperty("user.dir") + File.separator + "svn";

    @Autowired
    public SvnService(UserRepository userRepository, AESUtil aesUtil) {
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

            return userRepository.findByUserId(username).orElseThrow(() -> new UsernameNotFoundException("해당 아이디의 사용자를 찾을 수 없습니다"));
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
                ? "c-lab에 의해 commit. " + title
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

    public void checkoutAndModifyExcel(String position, String expendType, String cardType, String excelDate, String excelDetail, String excelAmount) throws Exception {
        User authenticatedUser = getAuthenticatedUser();
        SVNClientManager clientManager = createSVNClientManager(authenticatedUser);

        // SVN 리포지토리에서 파일 체크아웃
        SVNUpdateClient updateClient = clientManager.getUpdateClient();
        File checkoutDir = new File(localURL);
        updateClient.doCheckout(SVNURL.parseURIEncoded(svnURL), checkoutDir, SVNRevision.HEAD, SVNRevision.HEAD, SVNDepth.INFINITY, false);

        File excelFile = new File(localURL + File.separator + "2024년 경비지출내역.xlsx");

        // 엑셀 파일 수정
        try (FileInputStream fis = new FileInputStream(excelFile);
            Workbook workbook = new XSSFWorkbook(fis)) {
            // 첫 번째 시트를 가져옵니다.
            Sheet sheet = workbook.getSheetAt(0);

            // A열의 마지막 로우를 찾기
            int lastRowNum = findLastRowInColumn(sheet, 0);
            Row lastRow = sheet.getRow(lastRowNum);
            Row newRow = sheet.createRow(lastRowNum + 1);

            Object[] newRowData = {
                    position,
                    expendType,
                    excelDate,
                    authenticatedUser.getName(),
                    excelDetail,
                    cardType,
                    cardType.equals("개인카드") ? "N" : "",
                    Integer.parseInt(excelAmount)
            };

            // 마지막 행의 스타일과 데이터 복사
            copyRowStyles(lastRow, newRow);

            // 새로운 데이터 입력
            for (int i = 0; i < newRowData.length; i++) {
                Cell newCell = newRow.getCell(i);
                if (newRowData[i] instanceof String) {
                    newCell.setCellValue((String) newRowData[i]);
                } else if (newRowData[i] instanceof Number) {
                    newCell.setCellValue(((Number) newRowData[i]).doubleValue());
                } else if (newRowData[i] instanceof Boolean) {
                    newCell.setCellValue((Boolean) newRowData[i]);
                } else {
                    // 기본적으로 String으로 처리할 수 있는 경우
                    newCell.setCellValue(newRowData[i].toString());
                }
            }

            // 엑셀 파일 저장
            try (FileOutputStream fos = new FileOutputStream(excelFile)) {
                workbook.write(fos);
            }
        }

        // 변경된 파일을 SVN에 커밋
        commitChanges(clientManager, excelFile.getPath());
    }

    private void commitChanges(SVNClientManager clientManager, String filePath) throws Exception {
        File fileToCommit = new File(filePath);
        SVNCommitClient commitClient = clientManager.getCommitClient();
        SVNCommitInfo commitInfo = commitClient.doCommit(new File[]{fileToCommit}, false, "c-lab에 의해 commit.", null, null, false, false, SVNDepth.INFINITY);

        // 커밋이 성공적으로 완료되었는지 확인
        if (commitInfo.getErrorMessage() == null) {
            // 커밋 후 파일 삭제
            if (fileToCommit.delete()) {
                System.out.println("파일이 성공적으로 삭제되었습니다: " + filePath);
            } else {
                System.out.println("파일 삭제에 실패했습니다: " + filePath);
            }
        } else {
            System.out.println("커밋에 실패했습니다: " + commitInfo.getErrorMessage().getFullMessage());
        }
    }

    private void copyRowStyles(Row sourceRow, Row targetRow) {
        if (sourceRow == null) return;

        for (int i = 0; i < sourceRow.getLastCellNum(); i++) {
            Cell sourceCell = sourceRow.getCell(i);
            if (sourceCell == null) continue;

            Cell targetCell = targetRow.createCell(i);
            CellStyle newCellStyle = targetCell.getSheet().getWorkbook().createCellStyle();
            newCellStyle.cloneStyleFrom(sourceCell.getCellStyle());
            targetCell.setCellStyle(newCellStyle);

            if (sourceCell.getCellType() == CellType.STRING) {
                targetCell.setCellValue(sourceCell.getStringCellValue());
            } else if (sourceCell.getCellType() == CellType.NUMERIC) {
                targetCell.setCellValue(sourceCell.getNumericCellValue());
            } else if (sourceCell.getCellType() == CellType.BOOLEAN) {
                targetCell.setCellValue(sourceCell.getBooleanCellValue());
            } else if (sourceCell.getCellType() == CellType.FORMULA) {
                targetCell.setCellFormula(sourceCell.getCellFormula());
            }
        }
    }

    private int findLastRowInColumn(Sheet sheet, int columnIndex) {
        // getLastRowNum()은 실제로 사용된 마지막 행의 번호를 반환합니다 (0-based).
        int lastRow = sheet.getLastRowNum();

        // 마지막 로우부터 처음까지 역순으로 탐색하여 데이터를 가진 첫 번째 셀을 찾습니다.
        for (int i = lastRow; i >= 0; i--) {
            Row row = sheet.getRow(i);
            if (row != null) {
                Cell cell = row.getCell(columnIndex);
                if (cell != null && cell.getCellType() != CellType.BLANK) {
                    return i; // 마지막으로 데이터를 가진 행의 인덱스를 반환합니다.
                }
            }
        }
        // 모든 셀이 비어있는 경우, 첫 번째 행(0)을 반환합니다.
        return 0;
    }
}
