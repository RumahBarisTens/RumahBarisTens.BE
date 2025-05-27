package propensi.tens.bms.features.trainee_management.services;

import java.util.List;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import propensi.tens.bms.features.trainee_management.repositories.PeerReviewContentRepository;
import propensi.tens.bms.features.trainee_management.dto.response.PeerReviewQuestionResponse;

@Service @RequiredArgsConstructor
public class PeerReviewContentServiceImpl implements PeerReviewContentService {
    private final PeerReviewContentRepository repo;

    @Override
    public List<PeerReviewQuestionResponse> getAllQuestions() {
        return repo.findAllByOrderByQuestionNumberAsc().stream()
            .map(q -> new PeerReviewQuestionResponse(q.getQuestionNumber(), q.getText()))
            .toList();
    }
}
