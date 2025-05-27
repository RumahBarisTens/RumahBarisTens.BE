package propensi.tens.bms.features.trainee_management.services;

import propensi.tens.bms.features.trainee_management.dto.request.SubmitAssessmentRequestDTO;
import propensi.tens.bms.features.trainee_management.dto.request.EssayReviewRequestDTO;
import propensi.tens.bms.features.trainee_management.dto.response.SubmissionSummaryDTO;

import java.util.List;

public interface AssessmentSubmissionService {
    void submitAssessment(SubmitAssessmentRequestDTO dto);
    List<SubmissionSummaryDTO> getSubmissionSummariesByAssessmentId(Long assessmentId);
    void reviewEssaySubmission(EssayReviewRequestDTO dto);
    List<SubmissionSummaryDTO> getUserSubmissions(String username);
}
