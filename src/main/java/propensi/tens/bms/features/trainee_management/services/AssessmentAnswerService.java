package propensi.tens.bms.features.trainee_management.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import propensi.tens.bms.features.trainee_management.dto.response.AnswerKeyValueDTO;
import propensi.tens.bms.features.trainee_management.repositories.AssessmentAnswerRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AssessmentAnswerService {

    @Autowired
    private AssessmentAnswerRepository answerRepo;

    public List<AnswerKeyValueDTO> getKeyValueAnswersBySubmissionId(Long submissionId) {
        return answerRepo.findAnswersBySubmissionId(submissionId)
            .stream()
            .map(a -> {
                String kode = a.getQuestion().getId().toString();
                String jawaban;
                if (a.getSelectedOptionId() != null) {
                    jawaban = a.getSelectedOptionId().toString();
                } else {
                    jawaban = a.getEssayAnswer() != null ? a.getEssayAnswer() : "";
                }
                return new AnswerKeyValueDTO(kode, jawaban);
            })
            .collect(Collectors.toList());
    }
}
