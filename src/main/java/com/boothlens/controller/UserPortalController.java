package com.boothlens.controller;

import com.boothlens.dto.request.common.CommonRequest;
import com.boothlens.dto.response.ApiResponse;
import com.boothlens.dto.response.MeResponse;
import com.boothlens.dto.response.UserCreditsResponse;
import com.boothlens.dto.response.CommonRequestResponse;
import com.boothlens.dto.response.file.FileRecordDto;
import com.boothlens.dto.response.request.UserRequestDto;
import com.boothlens.dto.response.transaction.TransactionDto;
import com.boothlens.service.FileRecordService;
import com.boothlens.service.ProfileService;
import com.boothlens.service.TransactionService;
import com.boothlens.service.UserCreditsService;
import com.boothlens.service.UserRoleService;
import com.boothlens.service.UserRequestService;
import com.boothlens.util.SecurityUtils;
import com.boothlens.dto.response.announcement.AnnouncementDto;
import com.boothlens.service.AnnouncementService;
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
    private final ProfileService profileService;
    private final UserRoleService userRoleService;
    private final AnnouncementService announcementService;

    public UserPortalController(UserRequestService userRequestService,
                                FileRecordService fileRecordService,
                                TransactionService transactionService,
                                UserCreditsService userCreditsService,
                                ProfileService profileService,
                                UserRoleService userRoleService) {
        this.userRequestService = userRequestService;
        this.fileRecordService = fileRecordService;
        this.transactionService = transactionService;
        this.userCreditsService = userCreditsService;
        this.profileService = profileService;
        this.userRoleService = userRoleService;
        this.announcementService = announcementService;
    }

    /**
     * All-in-one payload for app initialization after login.
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<MeResponse>> getMyAllInfo() {
        Integer userId = requireCurrentUserId();
        MeResponse meResponse = MeResponse.builder()
                .requestDto(userRequestService.getCurrentUserRequests(userId))
                .creditsResponse(userCreditsService.getUserCredits(userId))
                .profileResponse(profileService.fetchProfile())
                .roleResponse(userRoleService.getCurrentUserRole())
                .build();
        return ResponseEntity.ok(ApiResponse.success("Fetched all current user information", meResponse));
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
     * 1.3) Create generic request with optional source reference and attachments.
     */
    @PostMapping(value = "/requests", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<CommonRequestResponse>> createCommonRequest(
            @Valid @ModelAttribute CommonRequest request,
            @RequestParam(value = "files", required = false) MultipartFile[] files) {
        Integer userId = requireCurrentUserId();
        CommonRequestResponse response = userRequestService.createCommonRequest(userId, request, files);
        return ResponseEntity.ok(ApiResponse.success("Request created", response));
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


    @GetMapping("/announcements")
    public ResponseEntity<ApiResponse<List<AnnouncementDto>>> getAllAnnouncements() {
        return ResponseEntity.ok(ApiResponse.success(
                "Fetched announcements",
                announcementService.getAllAnnouncements()
        ));
    }
}
