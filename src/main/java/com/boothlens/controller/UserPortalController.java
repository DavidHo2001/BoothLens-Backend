package com.boothlens.controller;

import com.boothlens.dto.request.contractor.CreateContractorRequestDto;
import com.boothlens.dto.response.ApiResponse;
import com.boothlens.dto.response.UserCreditsResponse;
import com.boothlens.dto.response.contractor.ContractorMatchResponseDto;
import com.boothlens.dto.response.file.FileRecordDto;
import com.boothlens.dto.response.request.UserRequestDto;
import com.boothlens.dto.response.transaction.TransactionDto;
import com.boothlens.service.FileRecordService;
import com.boothlens.service.TransactionService;
import com.boothlens.service.UserCreditsService;
import com.boothlens.service.UserRequestService;
import com.boothlens.util.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/user")
public class UserPortalController {

    private final UserRequestService userRequestService;
    private final FileRecordService fileRecordService;
    private final TransactionService transactionService;
    private final UserCreditsService userCreditsService;

    public UserPortalController(UserRequestService userRequestService,
                                FileRecordService fileRecordService,
                                TransactionService transactionService,
                                UserCreditsService userCreditsService) {
        this.userRequestService = userRequestService;
        this.fileRecordService = fileRecordService;
        this.transactionService = transactionService;
        this.userCreditsService = userCreditsService;
    }

    /**
     * 1) Get current user's requests.
     */
    @GetMapping("/requests")
    public ResponseEntity<ApiResponse<List<UserRequestDto>>> getMyRequests() {
        Integer userId = requireCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success(
                "Fetched current user requests",
                userRequestService.getCurrentUserRequests(userId)
        ));
    }

    /**
     * 1.1) Get files related to a request reference code.
     */
    @GetMapping("/requests/{referenceCode}/files")
    public ResponseEntity<ApiResponse<List<FileRecordDto>>> getRequestFiles(@PathVariable String referenceCode) {
        return ResponseEntity.ok(ApiResponse.success(
                "Fetched request files",
                fileRecordService.getFilesByReferenceCodeForCurrentUser(referenceCode)
        ));
    }

    /**
     * 1.2) Generate pre-signed download URL for one file record.
     */
    @GetMapping("/files/{fileRecordId}/download")
    public ResponseEntity<ApiResponse<Map<String, String>>> downloadFile(@PathVariable Integer fileRecordId) {
        String url = fileRecordService.getFileDownloadUrl(fileRecordId);
        return ResponseEntity.ok(ApiResponse.success("Download URL generated", Map.of("downloadUrl", url)));
    }

    /**
     * 1.3) Create contractor request with optional source reference and attachments.
     */
    @PostMapping(value = "/requests/contractor", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ContractorMatchResponseDto>> requestContractor(
            @Valid @ModelAttribute CreateContractorRequestDto request,
            @RequestParam(value = "files", required = false) MultipartFile[] files) {
        Integer userId = requireCurrentUserId();
        ContractorMatchResponseDto result = userRequestService.createContractorRequest(userId, request, files);
        return ResponseEntity.ok(ApiResponse.success("Contractor request created", result));
    }

    /**
     * 1.4) Current user's transaction history.
     */
    @GetMapping("/transactions")
    public ResponseEntity<ApiResponse<List<TransactionDto>>> getMyTransactions() {
        Integer userId = requireCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success(
                "Fetched transaction history",
                transactionService.getCurrentUserTransactions(userId)
        ));
    }

    /**
     * 1.5) Current user's credits.
     */
    @GetMapping("/credits")
    public ResponseEntity<ApiResponse<UserCreditsResponse>> getMyCredits() {
        Integer userId = requireCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success("Fetched credits info", userCreditsService.getUserCredits(userId)));
    }

    private Integer requireCurrentUserId() {
        Integer userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new IllegalArgumentException("User not authenticated");
        }
        return userId;
    }
}
