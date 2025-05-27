package propensi.tens.bms.features.trainee_management.dto.response;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionDetailResponse {
    private Long submissionId;
    private Long assessmentId;
    private String username;
    private String fullName;
    private Date submittedAt;
    private Double mcScore;
    private Double essayScore;
    private Double totalScore;
    private Boolean essayReviewed;
    private List<AnswerDetailResponse> answers;
}