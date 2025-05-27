// PeerReviewDashboardController.java
package propensi.tens.bms.features.trainee_management.controllers;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import propensi.tens.bms.features.account_management.dto.response.BaseResponseDTO;
import propensi.tens.bms.features.trainee_management.dto.response.*;
import propensi.tens.bms.features.trainee_management.services.PeerReviewDashboardService;

// @CrossOrigin(origins = "http://localhost:3002")
@RestController
@RequestMapping("/api/dashboard/peer-review")
public class PeerReviewDashboardController {

    @Autowired
    private PeerReviewDashboardService dashboardService;

    @GetMapping("/summary")
    public ResponseEntity<?> getDashboardSummary(
            @RequestParam(value = "timeRange", defaultValue = "this-month") String timeRange) {
        
        BaseResponseDTO<DashboardSummaryDTO> response = new BaseResponseDTO<>();
        try {
            System.out.println("TimeRange received: " + timeRange);
            DashboardSummaryDTO summary = dashboardService.getDashboardSummary(timeRange);
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Dashboard summary retrieved successfully");
            response.setTimestamp(new Date());
            response.setData(summary);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage(e.getMessage());
            response.setTimestamp(new Date());
            response.setData(null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/outlet-performance")
    public ResponseEntity<?> getOutletPerformance(
        @RequestParam(value = "timeRange", defaultValue = "this-month") String timeRange,
        @RequestParam(value = "month", required = false) String month) {
    
        BaseResponseDTO<List<OutletSummaryDTO>> response = new BaseResponseDTO<>();
        try {
            List<OutletSummaryDTO> outletPerformance = dashboardService.getOutletPerformance(timeRange, month);
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Outlet performance retrieved successfully");
            response.setTimestamp(new Date());
            response.setData(outletPerformance);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage(e.getMessage());
            response.setTimestamp(new Date());
            response.setData(null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/category-performance")
    public ResponseEntity<?> getCategoryPerformance(
            @RequestParam(value = "timeRange", defaultValue = "this-month") String timeRange) {
        
        BaseResponseDTO<List<QuestionSummaryDTO>> response = new BaseResponseDTO<>();
        try {
            List<QuestionSummaryDTO> categoryPerformance = dashboardService.getCategoryPerformance(timeRange);
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Category performance retrieved successfully");
            response.setTimestamp(new Date());
            response.setData(categoryPerformance);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage(e.getMessage());
            response.setTimestamp(new Date());
            response.setData(null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/baristas")
    public ResponseEntity<?> getBaristas(
            @RequestParam(value = "outlet", defaultValue = "all") String outlet,
            @RequestParam(value = "status", defaultValue = "all") String status,
            @RequestParam(value = "search", defaultValue = "") String search,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        
        BaseResponseDTO<PageResponseDTO<BaristaReviewSummaryDTO>> response = new BaseResponseDTO<>();
        try {
            PageResponseDTO<BaristaReviewSummaryDTO> baristas = dashboardService.getBaristas(outlet, status, search, page, size);
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Baristas retrieved successfully");
            response.setTimestamp(new Date());
            response.setData(baristas);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage(e.getMessage());
            response.setTimestamp(new Date());
            response.setData(null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/available-years")
    public ResponseEntity<?> getAvailableReviewYears() {
        try {
            List<Integer> years = dashboardService.getAvailablePeerReviewYears();
            BaseResponseDTO<List<Integer>> response = new BaseResponseDTO<>(
                HttpStatus.OK.value(),
                "Tahun tersedia berhasil diambil",
                new Date(),
                years
            );
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            BaseResponseDTO<Object> error = new BaseResponseDTO<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                e.getMessage(),
                new Date(),
                null
            );
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/score-trend")
    public ResponseEntity<?> getScoreTrend(
        @RequestParam(value = "timeRange", defaultValue = "this-month") String timeRange) {
    
        BaseResponseDTO<List<ScoreTrendDTO>> response = new BaseResponseDTO<>();
        try {
            List<ScoreTrendDTO> scoreTrend = dashboardService.getScoreTrend(timeRange);
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Score trend retrieved successfully");
            response.setTimestamp(new Date());
            response.setData(scoreTrend);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage(e.getMessage());
            response.setTimestamp(new Date());
            response.setData(null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // @GetMapping("/export")
    // public ResponseEntity<?> exportData(
    //         @RequestParam(value = "timeRange", defaultValue = "this-month") String timeRange,
    //         @RequestParam(value = "outlet", defaultValue = "all") String outlet,
    //         @RequestParam(value = "status", defaultValue = "all") String status) {
        
    //     try {
    //         byte[] excelBytes = dashboardService.exportPeerReviewData(timeRange, outlet, status);
            
    //         LocalDate today = LocalDate.now();
    //         DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    //         String fileName = "peer-review-report-" + today.format(formatter) + ".xlsx";
            
    //         HttpHeaders headers = new HttpHeaders();
    //         headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
    //         headers.setContentDispositionFormData("attachment", fileName);
    //         headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
            
    //         return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
    //     } catch (Exception e) {
    //         BaseResponseDTO<Object> response = new BaseResponseDTO<>();
    //         response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
    //         response.setMessage(e.getMessage());
    //         response.setTimestamp(new Date());
    //         response.setData(null);
    //         return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    //     }
    // }
}