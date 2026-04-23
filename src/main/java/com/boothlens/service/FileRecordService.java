package com.boothlens.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.boothlens.dto.response.file.FileRecordDto;
import com.boothlens.entity.UserRequest;
import com.boothlens.util.FileUtil;
import com.boothlens.util.SecurityUtils;
import com.boothlens.service.external.S3UploadService;
import com.boothlens.entity.FileRecord;
import com.boothlens.repository.FileRecordRepository;
import com.boothlens.repository.UserRequestRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FileRecordService {
    
    @Autowired
    private FileRecordRepository fileRecordRepository;

    @Autowired
    private S3UploadService s3UploadService;

    @Autowired
    private UserRequestRepository userRequestRepository;

    /**
     * Upload multiple files and create file records under one reference code.
     */
    @Transactional
    public List<FileRecord> uploadImagesAndSaveRecords(MultipartFile[] images, Integer userId, String referenceCode) {
        List<FileRecord> records = new ArrayList<>();
        if (images == null || images.length == 0) {
            return records;
        }
        Integer imageIndex = 1;
        for (MultipartFile image : images) {
            try {
                // Upload to S3 first, then persist file metadata.
                String fileName = buildImageFileName(image.getOriginalFilename(), referenceCode, imageIndex);
                String s3Key = s3UploadService.uploadImageWithFileName(image, referenceCode, fileName);

                FileRecord record = buildFileRecord(userId, s3Key, fileName, image.getContentType(), referenceCode);
                records.add(fileRecordRepository.saveAndFlush(record));
                imageIndex++;
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload image file", e);
            }
        }
        return records;
    }

    /**
     * 上傳生成的 PDF 並存入資料庫
     */
    @Transactional
    public FileRecord uploadPdfAndSaveRecord(byte[] pdfData, String fileName, Integer userId, String referenceCode) {
        // 1. 上傳到 S3，取得 Key
        String s3Key = s3UploadService.uploadPdf(pdfData, referenceCode, fileName);

        // 2. 存入資料庫
        FileRecord record = buildFileRecord(userId, s3Key, fileName, "application/pdf", referenceCode);
        return fileRecordRepository.save(record);
    }

    /**
     * 取得檔案的限時下載網址 (給 Controller 呼叫用)
     */
    public String getFileDownloadUrl(Integer fileRecordId) {
        FileRecord record = fileRecordRepository.findById(fileRecordId)
                .orElseThrow(() -> new RuntimeException("File not found"));

        Integer currentUserId = SecurityUtils.getCurrentUserId();
        Integer currentRole = SecurityUtils.getCurrentUserRole();
        boolean isAdmin = currentRole != null && (currentRole == 3 || currentRole == 4);
        if (currentUserId == null || (!isAdmin && !currentUserId.equals(record.getUserId()))) {
            throw new IllegalArgumentException("You do not have permission to download this file");
        }

        return s3UploadService.generatePresignedUrl(record.getFileKey(), 60);
    }

    public List<FileRecordDto> getFilesByReferenceCodeForCurrentUser(String referenceCode) {
        if (!StringUtils.hasText(referenceCode)) {
            throw new IllegalArgumentException("Reference code is required");
        }

        UserRequest request = userRequestRepository.findByReferenceCode(referenceCode);
        if (request == null) {
            throw new IllegalArgumentException("Request not found");
        }

        Integer currentUserId = SecurityUtils.getCurrentUserId();
        Integer currentRole = SecurityUtils.getCurrentUserRole();
        boolean isAdmin = currentRole != null && (currentRole == 3 || currentRole == 4);
        if (currentUserId == null || (!isAdmin && !request.getUserId().equals(currentUserId))) {
            throw new IllegalArgumentException("You do not have permission to view files for this request");
        }

        return fileRecordRepository.findByReferenceCodeOrderByCreatedAtDesc(referenceCode).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private String buildImageFileName(String originalFilename, String referenceCode, Integer imageIndex) {
        if (imageIndex == null) {
            return "image_" + referenceCode + "." + FileUtil.extractExtension(originalFilename);
        }
        return "image_" + referenceCode + "_" + imageIndex + "." + FileUtil.extractExtension(originalFilename);
    }

    private FileRecord buildFileRecord(Integer userId, String s3Key, String fileName, String fileType, String referenceCode) {
        FileRecord record = new FileRecord();
        record.setUserId(userId);
        record.setFileKey(s3Key);
        record.setFileName(fileName);
        record.setFileType(fileType);
        record.setReferenceCode(referenceCode);
        return record;
    }

    private FileRecordDto toDto(FileRecord record) {
        return FileRecordDto.builder()
                .id(record.getId())
                .referenceCode(record.getReferenceCode())
                .fileName(record.getFileName())
                .fileType(record.getFileType())
                .createdAt(record.getCreatedAt())
                .build();
    }
}
