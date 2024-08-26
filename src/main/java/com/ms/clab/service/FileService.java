package com.ms.clab.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 앞으로 File 관련 로직이 복잡해질 것으로 고려하여 Service로 사용
 */
@Service
public class FileService {

    private final String uploadDirectory;

    public FileService(@Value("${file.upload-directory}") String uploadDirectory) {
        this.uploadDirectory = uploadDirectory;
    }

    /**
     * 파일을 서버에 저장
     * @param file 업로드할 파일
     * @return 파일 경로
     * @throws IOException 파일 저장 중 오류 발생 시
     */
    public String saveFile(MultipartFile file) throws IOException {
        validateFile(file);
        createDirectoryIfNotExists(uploadDirectory);

        String filePath = uploadDirectory + File.separator + file.getOriginalFilename();
        saveMultipartFile(file, filePath);

        return filePath;
    }

    private void validateFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("업로드할 파일이 선택되지 않았습니다.");
        }
    }

    private void createDirectoryIfNotExists(String directory) throws IOException {
        Path dirPath = Paths.get(directory);
        if (Files.notExists(dirPath)) {
            Files.createDirectories(dirPath);
        }
    }

    private void saveMultipartFile(MultipartFile file, String filePath) throws IOException {
        Path destination = Paths.get(filePath);
        try {
            file.transferTo(destination.toFile());
        } catch (IOException e) {
            throw new IOException("파일 저장 중 오류가 발생했습니다: " + file.getOriginalFilename(), e);
        }
    }
}