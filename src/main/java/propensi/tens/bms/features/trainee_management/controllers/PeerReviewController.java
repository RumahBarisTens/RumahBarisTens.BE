// PeerReviewController.java
package propensi.tens.bms.features.trainee_management.controllers;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import propensi.tens.bms.features.trainee_management.dto.request.CreatePeerReviewSubmissionRequest;
import propensi.tens.bms.features.trainee_management.dto.response.*;
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
}
