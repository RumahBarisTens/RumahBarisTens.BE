package propensi.tens.bms.features.trainee_management.services;

import java.util.List;
import propensi.tens.bms.features.trainee_management.dto.response.PeerReviewQuestionResponse;

public interface PeerReviewContentService {
    List<PeerReviewQuestionResponse> getAllQuestions();
}
