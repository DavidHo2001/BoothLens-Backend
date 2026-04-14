package com.boothlens.service.analysis;

import com.boothlens.dto.request.booth.BoothAnalysisRequest;
import com.boothlens.dto.response.booth.BoothAnalysisResponse;
import com.boothlens.dto.jwt.JwtProfile;
import com.boothlens.dto.response.booth.BoothQuotationCalculation;
import com.boothlens.dto.response.booth.BoothQuotationItem;
import com.boothlens.service.UserRequestService;
import com.boothlens.service.UserCreditsService;
import com.boothlens.service.FileRecordService;
import com.boothlens.service.analysis.CalculationService;
import com.boothlens.service.PdfGeneratorService;
import com.boothlens.service.external.OpenRouterService;
import com.boothlens.entity.UserRequest;
import com.boothlens.entity.FileRecord;
import com.boothlens.util.SecurityUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Slf4j
public class BoothAnalysisService {

    @Autowired
    private OpenRouterService openRouterService;

    @Autowired
    private UserRequestService userRequestService;

    @Autowired
    private UserCreditsService userCreditsService;

    @Autowired
    private FileRecordService fileRecordService;

    @Autowired
    private CalculationService calculationService;

    @Autowired
    private PdfGeneratorService pdfGeneratorService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${prompt.system}")
    private String systemPrompt;

    @Value("${prompt.user}")
    private String userPromptTemplate;

    public static final int TOKEN_CONSUMPTION_PER_ANALYSIS = 1;


    //TODO: async the open router calling, add refundToken logic (處理失敗的補償邏輯 (退還 Token、更新狀態))
    public BoothAnalysisResponse analyzeBooth(MultipartFile[] images, BoothAnalysisRequest request) {
        log.info("Starting analysis for booth size: {}x{}x{} {}",
                request.getBoothWidth(), request.getBoothLength(), request.getBoothHeight(), request.getBoothSizeUnit());

        // Step 1. Validate user
        JwtProfile jwtProfile = SecurityUtils.getCurrentJwtProfile();
        Integer userId = jwtProfile.getUserId();
        boolean isAdmin = jwtProfile.getRole() == 3 || jwtProfile.getRole() == 4;

        // Step 2. Build Prompt
        String userPromptText = buildUserPrompt(request);

        // Step 3. Save user request
        UserRequest savedUserRequest = userRequestService.createUserBoothAnalysisRequest(userId, userPromptText);
        // Step 4. Consume token
        if(!isAdmin) {
            userCreditsService.consumeToken(userId, TOKEN_CONSUMPTION_PER_ANALYSIS, 0, savedUserRequest.getReferenceCode());
        }
        // Step 5. Upload images to S3 via FileRecordService
        try {
            List<FileRecord> savedImageRecords = fileRecordService.uploadImagesAndSaveRecords(
                    images, userId, savedUserRequest.getReferenceCode()
            );
            log.info("Saved {} images to database and S3", savedImageRecords.size());
        } catch (Exception e) {
            log.error("Failed to upload images", e);
            throw new RuntimeException("Failed to upload images", e);
        }

        // Step 6. Send request to OpenRouter
        String jsonResult = openRouterService.sendRequestWithImage(images, systemPrompt, userPromptText);
        BoothAnalysisResponse response;
        try {
            response = objectMapper.readValue(jsonResult, BoothAnalysisResponse.class);
        } catch (Exception e) {
            log.error("Failed to parse OpenRouter response: {}", e.getMessage());
            throw new RuntimeException("Failed to parse analysis response", e);
        }

        // Step 7. Calculate cost (TO COMPLETE THE LOGIC)
        BoothQuotationCalculation calculationResult = calculationService.dummyCalculateBoothQuotation();

        // Step 8. Generate PDF async
        // 將 PDF 生成與上傳交給背景執行，不阻擋 API 回應
        generateAndUploadPdfAsync(calculationResult, userId, savedUserRequest.getId(), savedUserRequest.getReferenceCode());

        return response;
    }

    @Async
    public void generateAndUploadPdfAsync(BoothQuotationCalculation calculationResult, Integer userId, Integer requestId, String referenceCode) {
        try {
            log.info("Start generating PDF in background for reference: {}", referenceCode);
            
            // 🌟 呼叫 PDF Generator，傳入 calculationResult
            byte[] pdfBytes = pdfGeneratorService.generateQuotationPdf(referenceCode, userId, calculationResult);
            
            String fileName = "BoothLens_Quotation_" + referenceCode + ".pdf";

            FileRecord pdfRecord = fileRecordService.uploadPdfAndSaveRecord(pdfBytes, fileName, userId, referenceCode);
            log.info("Successfully generated and uploaded PDF. FileRecord ID: {}", pdfRecord.getId());
            
            userRequestService.updateUserRequestStatus(userId, requestId, "COMPLETED");

        } catch (Exception e) {
            log.error("Failed to generate or upload PDF for reference: {}", referenceCode, e);
        }
    }

    private String buildUserPrompt(BoothAnalysisRequest request) {
        return userPromptTemplate
                .replace("{boothWidth}", String.valueOf(request.getBoothWidth()))
                .replace("{boothLength}", String.valueOf(request.getBoothLength()))
                .replace("{boothHeight}", String.valueOf(request.getBoothHeight()))
                .replace("{boothSizeUnit}", request.getBoothSizeUnit())
                .replace("{boothWidth * boothLength}", String.valueOf(request.getBoothWidth() * request.getBoothLength()))
                .replace("{requestInfo}", request.getRequestInfo() != null ? request.getRequestInfo() : "No additional information provided");
    }


    
}
