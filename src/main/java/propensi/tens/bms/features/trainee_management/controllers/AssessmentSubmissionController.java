package propensi.tens.bms.features.trainee_management.controllers;

import propensi.tens.bms.features.trainee_management.dto.request.SubmitAssessmentRequestDTO;
import propensi.tens.bms.features.trainee_management.dto.request.EssayReviewRequestDTO;
import propensi.tens.bms.features.trainee_management.dto.response.SubmissionSummaryDTO;
import propensi.tens.bms.features.trainee_management.services.AssessmentSubmissionService;
import propensi.tens.bms.features.account_management.dto.response.BaseResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.persistence.EntityNotFoundException;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/trainee/assessment")
public class AssessmentSubmissionController {

    @Autowired
    private AssessmentSubmissionService submissionService;

    @PostMapping("/submit")
    public ResponseEntity<BaseResponseDTO<String>> submit(@RequestBody SubmitAssessmentRequestDTO dto) {
        BaseResponseDTO<String> resp = new BaseResponseDTO<>();
        try {
            submissionService.submitAssessment(dto);
            resp.setStatus(201);
            resp.setMessage("Assessment berhasil dikirim");
            resp.setTimestamp(new Date());
            resp.setData("submitted");
            return ResponseEntity.status(201).body(resp);
        } catch (Exception e) {
            resp.setStatus(400);
            resp.setMessage("Gagal mengirim assessment: " + e.getMessage());
            resp.setTimestamp(new Date());
            return ResponseEntity.badRequest().body(resp);
        }
    }
    
    @PostMapping("/review-essay")
    public ResponseEntity<BaseResponseDTO<String>> reviewEssay(@RequestBody EssayReviewRequestDTO dto) {
        BaseResponseDTO<String> resp = new BaseResponseDTO<>();
        try {
            submissionService.reviewEssaySubmission(dto);
            resp.setStatus(200);
            resp.setMessage("Penilaian essay berhasil disimpan");
            resp.setTimestamp(new Date());
            resp.setData("reviewed");
            return ResponseEntity.ok(resp);
        } catch (EntityNotFoundException e) {
            resp.setStatus(404);
            resp.setMessage("Gagal menyimpan penilaian: " + e.getMessage());
            resp.setTimestamp(new Date());
            return ResponseEntity.status(404).body(resp);
        } catch (Exception e) {
            resp.setStatus(400);
            resp.setMessage("Gagal menyimpan penilaian: " + e.getMessage());
            resp.setTimestamp(new Date());
            return ResponseEntity.badRequest().body(resp);
        }
    }
    
    @GetMapping("/{assessmentId}/summaries")
    public ResponseEntity<BaseResponseDTO<List<SubmissionSummaryDTO>>> getSummaries(
            @PathVariable("assessmentId") Long assessmentId
    ) {
        BaseResponseDTO<List<SubmissionSummaryDTO>> resp = new BaseResponseDTO<>();
        try {
            List<SubmissionSummaryDTO> data =
                submissionService.getSubmissionSummariesByAssessmentId(assessmentId);
            resp.setStatus(200);
            resp.setMessage("OK");
            resp.setTimestamp(new Date());
            resp.setData(data);
            return ResponseEntity.ok(resp);
        } catch (EntityNotFoundException e) {
            resp.setStatus(404);
            resp.setMessage(e.getMessage());
            resp.setTimestamp(new Date());
            return ResponseEntity.status(404).body(resp);
        } catch (Exception e) {
            resp.setStatus(500);
            resp.setMessage("Internal server error: " + e.getMessage());
            resp.setTimestamp(new Date());
            return ResponseEntity.status(500).body(resp);
        }
    }

    @GetMapping("/history/{username}")
    public ResponseEntity<BaseResponseDTO<List<SubmissionSummaryDTO>>> getHistory(
            @PathVariable("username") String username
    ) {
        BaseResponseDTO<List<SubmissionSummaryDTO>> resp = new BaseResponseDTO<>();
        try {
            List<SubmissionSummaryDTO> data =
                submissionService.getUserSubmissions(username);
            resp.setStatus(200);
            resp.setMessage("OK");
            resp.setTimestamp(new Date());
            resp.setData(data);
            return ResponseEntity.ok(resp);
        } catch (EntityNotFoundException e) {
            resp.setStatus(404);
            resp.setMessage(e.getMessage());
            resp.setTimestamp(new Date());
            return ResponseEntity.status(404).body(resp);
        } catch (Exception e) {
            resp.setStatus(500);
            resp.setMessage("Internal server error: " + e.getMessage());
            resp.setTimestamp(new Date());
            return ResponseEntity.status(500).body(resp);
        }
    }
}
