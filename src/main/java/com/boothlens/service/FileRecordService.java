package com.boothlens.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.boothlens.util.FileUtil;
import com.boothlens.service.external.S3UploadService;
import com.boothlens.entity.FileRecord;
import com.boothlens.repository.FileRecordRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class FileRecordService {
    
    @Autowired
    private FileRecordRepository fileRecordRepository;

    @Autowired
    private S3UploadService s3UploadService;


    // 上傳多張圖片並存入資料庫
    @Transactional
    public List<FileRecord> uploadImagesAndSaveRecords(MultipartFile[] images, Integer userId, String referenceCode) throws IOException {
        List<FileRecord> records = new ArrayList<>();
        if (images == null || images.length == 0) return records;
        Integer imageIndex = 1;
        for (MultipartFile image : images) {
            // 1. 上傳到 S3，取得 Key
            String fileName = buildImageFileName(image.getOriginalFilename(), referenceCode, imageIndex); //用系統生成名稱
            String s3Key = s3UploadService.uploadImageWithFileName(image, referenceCode, fileName);

            // 2. 存入資料庫
            FileRecord record = new FileRecord();
            record.setUserId(userId);
            record.setFileKey(s3Key);
            //record.setFileName(image.getOriginalFilename());
            record.setFileName(fileName);
            record.setFileType(image.getContentType());
            
            records.add(fileRecordRepository.saveAndFlush(record));
            imageIndex = imageIndex + 1;
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
        FileRecord record = new FileRecord();
        record.setUserId(userId);
        record.setFileKey(s3Key);
        record.setFileName(fileName);
        record.setFileType("application/pdf");

        return fileRecordRepository.save(record);
    }

    /**
     * 取得檔案的限時下載網址 (給 Controller 呼叫用)
     */
    public String getFileDownloadUrl(Integer fileRecordId) {
        FileRecord record = fileRecordRepository.findById(fileRecordId)
                .orElseThrow(() -> new RuntimeException("File not found"));
        // 產生 60 分鐘有效的網址
        return s3UploadService.generatePresignedUrl(record.getFileKey(), 60); 
    }

    private String buildImageFileName(String originalFilename, String referenceCode, Integer imageIndex) {
        if (imageIndex == null) {
            return "image_" + referenceCode + "." + FileUtil.extractExtension(originalFilename);
        }
        return "image_" + referenceCode + "_" + imageIndex + "." + FileUtil.extractExtension(originalFilename);
    }
}
