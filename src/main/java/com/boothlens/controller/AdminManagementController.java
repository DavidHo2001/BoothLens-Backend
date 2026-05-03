package com.boothlens.controller;

import com.boothlens.dto.request.admin.AnnouncementUpsertRequestDto;
import com.boothlens.dto.request.admin.TransactionSearchRequestDto;
import com.boothlens.dto.request.admin.UpdateUserRequestAdminDto;
import com.boothlens.dto.request.admin.UserCreditsAdjustRequestDto;
import com.boothlens.dto.request.admin.UserCreditsSearchRequestDto;
import com.boothlens.dto.request.admin.UserRequestSearchRequestDto;
import com.boothlens.dto.response.ApiResponse;
import com.boothlens.dto.response.UserCreditsResponse;
import com.boothlens.dto.response.announcement.AnnouncementDto;
import com.boothlens.dto.response.pagination.PaginatedResponseDto;
import com.boothlens.dto.response.request.UserRequestDto;
import com.boothlens.dto.response.transaction.TransactionDto;
import com.boothlens.service.AnnouncementService;
import com.boothlens.service.TransactionService;
import com.boothlens.service.UserCreditsService;
import com.boothlens.service.UserRequestService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminManagementController {

    private final UserRequestService userRequestService;
    private final AnnouncementService announcementService;
    private final TransactionService transactionService;
    private final UserCreditsService userCreditsService;

    public AdminManagementController(UserRequestService userRequestService,
                                     AnnouncementService announcementService,
                                     TransactionService transactionService,
                                     UserCreditsService userCreditsService) {
        this.userRequestService = userRequestService;
        this.announcementService = announcementService;
        this.transactionService = transactionService;
        this.userCreditsService = userCreditsService;
    }

    /**
     * 2.1) Get all requests with keyword/status/pagination.
     */
    @PostMapping("/requests/search")
    public ResponseEntity<ApiResponse<PaginatedResponseDto<UserRequestDto>>> searchRequests(
            @RequestBody UserRequestSearchRequestDto request) {
        return ResponseEntity.ok(ApiResponse.success(
                "Fetched requests",
                userRequestService.adminSearch(request)
        ));
    }

    /**
     * 2.2) Update request status and reply (stored in resultInfo).
     */
    @PatchMapping("/requests/{requestId}")
    public ResponseEntity<ApiResponse<UserRequestDto>> updateRequest(
            @PathVariable Integer requestId,
            @RequestBody UpdateUserRequestAdminDto request) {
        return ResponseEntity.ok(ApiResponse.success(
                "Updated request",
                userRequestService.adminUpdateRequest(requestId, request)
        ));
    }

    /**
     * 2.3) CRUD announcements.
     */
    @PostMapping("/announcements")
    public ResponseEntity<ApiResponse<AnnouncementDto>> createAnnouncement(
            @Valid @RequestBody AnnouncementUpsertRequestDto request) {
        return ResponseEntity.ok(ApiResponse.success(
                "Announcement created",
                announcementService.createAnnouncement(request)
        ));
    }

    @PutMapping("/announcements/{id}")
    public ResponseEntity<ApiResponse<AnnouncementDto>> updateAnnouncement(
            @PathVariable Integer id,
            @Valid @RequestBody AnnouncementUpsertRequestDto request) {
        return ResponseEntity.ok(ApiResponse.success(
                "Announcement updated",
                announcementService.updateAnnouncement(id, request)
        ));
    }

    @DeleteMapping("/announcements/{id}")
    public ResponseEntity<ApiResponse<String>> deleteAnnouncement(@PathVariable Integer id) {
        announcementService.deleteAnnouncement(id);
        return ResponseEntity.ok(ApiResponse.success("Announcement deleted"));
    }

    /**
     * 2.4) Get all user transaction history (with search/pagination).
     */
    @PostMapping("/transactions/search")
    public ResponseEntity<ApiResponse<PaginatedResponseDto<TransactionDto>>> searchTransactions(
            @RequestBody TransactionSearchRequestDto request) {
        return ResponseEntity.ok(ApiResponse.success(
                "Fetched transactions",
                transactionService.adminSearch(request)
        ));
    }

    /**
     * 2.5) Get all user credits info (with search/pagination).
     */
    @PostMapping("/credits/search")
    public ResponseEntity<ApiResponse<PaginatedResponseDto<UserCreditsResponse>>> searchCredits(
            @RequestBody UserCreditsSearchRequestDto request) {
        return ResponseEntity.ok(ApiResponse.success(
                "Fetched user credits",
                userCreditsService.adminSearch(request)
        ));
    }

    /**
     * 2.6) Adjust user credits and write transaction history.
     */
    @PostMapping("/credits/adjust")
    public ResponseEntity<ApiResponse<UserCreditsResponse>> adjustCredits(
            @RequestBody UserCreditsAdjustRequestDto request) {
        return ResponseEntity.ok(ApiResponse.success(
                "Adjusted user credits",
                userCreditsService.adminAdjustCredits(request)
        ));
    }
}
