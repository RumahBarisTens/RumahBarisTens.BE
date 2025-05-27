package propensi.tens.bms.features.shift_management.admin_dashboard.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import propensi.tens.bms.features.account_management.dto.response.OutletResponseDTO;
import propensi.tens.bms.features.shift_management.admin_dashboard.dto.request.AdminDashboardFilterRequestDTO;
import propensi.tens.bms.features.shift_management.admin_dashboard.dto.response.AdminBaristaShiftDetailDTO;
import propensi.tens.bms.features.shift_management.admin_dashboard.dto.response.BaristaShiftSummaryDTO;
import propensi.tens.bms.features.shift_management.admin_dashboard.services.AdminDashboardService;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/admin")
@Validated
public class AdminDashboardController {

    @Autowired
    private AdminDashboardService adminDashboardService;

    private final Logger logger = LoggerFactory.getLogger(AdminDashboardController.class);

    @GetMapping("/dashboard/summary")
    public ResponseEntity<ApiResponse<List<BaristaShiftSummaryDTO>>> getShiftSummary(
            @Valid @ModelAttribute AdminDashboardFilterRequestDTO request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        logger.info("===== START getShiftSummary =====");
        logger.info("Received request: startDate={}, endDate={}, outletId={}, baristaName={}",
                request.getStartDate(), request.getEndDate(), request.getOutletId(), request.getBaristaName());

        try {
            List<BaristaShiftSummaryDTO> summaries = adminDashboardService.getShiftSummary(request);
            logger.info("Service returned {} summary(ies)", summaries.size());

            ApiResponse<List<BaristaShiftSummaryDTO>> response = new ApiResponse<>();
            response.setSuccess(true);
            response.setData(summaries);
            response.setMessage(summaries.isEmpty() ? "Tidak ada data untuk filter yang dipilih" : "Data berhasil dimuat");

            logger.info("===== END getShiftSummary SUCCESS =====");
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            logger.error("Validation error in getShiftSummary: {}", e.getMessage());
            return buildErrorResponse("Validation failed: " + e.getMessage(), null, 400);
        } catch (Exception e) {
            logger.error("===== ERROR getShiftSummary =====", e);
            return buildErrorResponse("Failed to load data: " + e.getMessage(), null, 500);
        }
    }

    @GetMapping("/barista/{baristaId}/shifts")
    public ResponseEntity<ApiResponse<List<AdminBaristaShiftDetailDTO>>> getBaristaShiftDetails(
            @PathVariable("baristaId") String baristaId,
            @Valid @ModelAttribute AdminDashboardFilterRequestDTO request,
            @RequestParam(value = "outletId", required = false) Long outletId) {
        logger.info("===== START getBaristaShiftDetails =====");
        logger.info("Received request: baristaId={}, startDate={}, endDate={}, outletId={}",
                baristaId, request.getStartDate(), request.getEndDate(), outletId);

        try {
            // Validasi baristaId
            UUID baristaUuid;
            try {
                baristaUuid = UUID.fromString(baristaId);
                logger.info("Parsed baristaId: {}", baristaUuid);
            } catch (IllegalArgumentException e) {
                logger.error("Invalid baristaId format: {}", baristaId, e);
                return buildErrorResponse("Invalid baristaId format: Expected UUID", null, 400);
            }

            // Set outletId ke request
            request.setOutletId(outletId);

            List<AdminBaristaShiftDetailDTO> shiftDetails = adminDashboardService.getBaristaShiftDetails(baristaUuid, request);
            logger.info("Service returned {} shift detail(s)", shiftDetails.size());

            ApiResponse<List<AdminBaristaShiftDetailDTO>> response = new ApiResponse<>();
            response.setSuccess(true);
            response.setData(shiftDetails);
            response.setMessage(shiftDetails.isEmpty() ? "Tidak ada data shift untuk periode ini" : "Data berhasil dimuat");

            logger.info("===== END getBaristaShiftDetails SUCCESS =====");
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            logger.error("Validation error in getBaristaShiftDetails: {}", e.getMessage());
            return buildErrorResponse("Validation failed: " + e.getMessage(), null, 400);
        } catch (Exception e) {
            logger.error("===== ERROR getBaristaShiftDetails =====", e);
            return buildErrorResponse("Failed to load data: " + e.getMessage(), null, 500);
        }
    }

    @GetMapping("/outlets")
    public ResponseEntity<ApiResponse<List<OutletResponseDTO>>> getAllOutlets() {
        logger.info("===== START getAllOutlets =====");

        try {
            List<OutletResponseDTO> outlets = adminDashboardService.getAllOutlets();
            logger.info("Service returned {} outlet(s)", outlets.size());

            ApiResponse<List<OutletResponseDTO>> response = new ApiResponse<>();
            response.setSuccess(true);
            response.setData(outlets);
            response.setMessage(outlets.isEmpty() ? "Tidak ada outlet ditemukan" : "Data outlet berhasil dimuat");

            logger.info("===== END getAllOutlets SUCCESS =====");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("===== ERROR getAllOutlets =====", e);
            return buildErrorResponse("Failed to load outlet data: " + e.getMessage(), null, 500);
        }
    }

    private <T> ResponseEntity<ApiResponse<T>> buildErrorResponse(String message, T data, int status) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setSuccess(false);
        response.setData(data);
        response.setMessage(message);
        return ResponseEntity.status(status).body(response);
    }
}

class ApiResponse<T> {
    private boolean success;
    private T data;
    private String message;

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}