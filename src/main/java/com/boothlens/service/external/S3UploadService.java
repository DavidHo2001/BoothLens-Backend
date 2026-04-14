package com.boothlens.service.external;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import com.boothlens.util.FileUtil;
import org.springframework.data.util.Pair;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

@Service
public class S3UploadService {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    public S3UploadService(S3Client s3Client, S3Presigner s3Presigner) {
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
    }

    @Value("${aws.s3.bucket}")
    private String bucket;

    @Value("${aws.s3.image-folder-prefix}")
    private String imageFolderPrefix;

    @Value("${aws.s3.pdf-folder-prefix}")
    private String pdfFolderPrefix;

    /**
     * 上傳圖片 (MultipartFile)
     */
    //指定file名
    public String uploadImageWithFileName(MultipartFile file, String referenceCode, String fileName) throws IOException {
        String key = generateKey(imageFolderPrefix, referenceCode, fileName);
        uploadToS3(key, file.getContentType(), RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        return key;        
    }

    //用原file名
    public String uploadImage(MultipartFile file, String referenceCode) throws IOException {
        String key = generateKey(imageFolderPrefix, referenceCode, file.getOriginalFilename());
        uploadToS3(key, file.getContentType(), RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        return key;
    }

    /**
     * 上傳 PDF (Byte Array) - 給系統自動生成的 PDF 使用
     */
    public String uploadPdf(byte[] pdfData, String referenceCode, String fileName) {
        String key = generateKey(pdfFolderPrefix, referenceCode, fileName);
        uploadToS3(key, "application/pdf", RequestBody.fromBytes(pdfData));
        return key;
    }

    /**
     * 共用的 S3 上傳邏輯 (私有檔案，不設定 ACL)
     */
    private void uploadToS3(String key, String contentType, RequestBody requestBody) {
        PutObjectRequest putReq = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(contentType)
                .build(); // ⚠️ 沒有 PUBLIC_READ，確保檔案是私有的

        try {
            s3Client.putObject(putReq, requestBody);
        } catch (S3Exception e) {
            throw new RuntimeException("S3 upload failed: " + e.awsErrorDetails().errorMessage(), e);
        }
    }

    /**
     * 🌟 產生限時下載網址 (Pre-signed URL)
     */
    public String generatePresignedUrl(String key, int minutes) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(minutes))
                .getObjectRequest(getObjectRequest)
                .build();

        return s3Presigner.presignGetObject(presignRequest).url().toString();
    }

    //Key係用黎搵S3 File, File Name係比User睇/Download用 (internal use only)
    private String generateKey(String folderPrefix, String referenceCode, String filename) {
        if (referenceCode != null && !referenceCode.isBlank()) {
            folderPrefix += referenceCode + "/";  //AWS 用Flat object storage, 冇所謂folder structure(only console 比你睇係folder咁)
        }
        String ext = FileUtil.extractExtension(filename);
        return folderPrefix + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8) + (ext.isEmpty() ? "" : "." + ext);
    }
}
