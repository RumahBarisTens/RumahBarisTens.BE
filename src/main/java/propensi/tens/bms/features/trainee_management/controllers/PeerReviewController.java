// PeerReviewController.java
package propensi.tens.bms.features.trainee_management.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import propensi.tens.bms.features.trainee_management.dto.request.CreatePeerReviewSubmissionRequest;
import propensi.tens.bms.features.trainee_management.dto.response.*;
import propensi.tens.bms.features.trainee_management.models.PeerReviewSubmission;
import propensi.tens.bms.features.trainee_management.services.*;

@RestController
@RequestMapping("/api/peer-review")
@RequiredArgsConstructor
public class PeerReviewController {

    private final PeerReviewContentService contentService;
    private final PeerReviewSubmissionService submissionService;

    @GetMapping("/questions")
    public ResponseEntity<List<PeerReviewQuestionResponse>> questions() {
        return ResponseEntity.ok(contentService.getAllQuestions());
    }

    @PostMapping("/submit")
    public ResponseEntity<PeerReviewSubmissionResponse> submit(
        @Validated @RequestBody CreatePeerReviewSubmissionRequest req
    ) {
        return ResponseEntity.ok(submissionService.submit(req));
    }

    @GetMapping("/submissions/reviewer/{username}")
    public ResponseEntity<List<PeerReviewSubmissionResponse>> byReviewer(
        @PathVariable String username
    ) {
        return ResponseEntity.ok(submissionService.getByReviewer(username));
    }

    // @GetMapping("/history/{username}")
    // public ResponseEntity<List<PeerReviewSubmission>> getHistoryByUsername(@PathVariable String username) {
    //     try {
    //         System.out.println("APU");
    //         List<PeerReviewSubmission> history = submissionService.getPeerReviewHistoryByUsername(username);
    //         if (history.isEmpty()) {
    //             return ResponseEntity.noContent().build();
    //         }
    //         return ResponseEntity.ok(history);
    //     } catch (IllegalArgumentException e) {
    //         throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
    //     } catch (Exception e) {
    //         // Log exception di sini untuk debugging
    //         throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Gagal mengambil riwayat peer review", e);
    //     }
    // }
}
