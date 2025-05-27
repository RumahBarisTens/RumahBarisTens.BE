package propensi.tens.bms.features.trainee_management.controllers;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import propensi.tens.bms.features.account_management.dto.response.BaseResponseDTO;
import propensi.tens.bms.features.trainee_management.dto.response.AnswerKeyValueDTO;
import propensi.tens.bms.features.trainee_management.models.Assessment;
import propensi.tens.bms.features.trainee_management.models.AssessmentQuestion;
import propensi.tens.bms.features.trainee_management.models.QuestionOption;
import propensi.tens.bms.features.trainee_management.repositories.AssessmentRepository;
import propensi.tens.bms.features.trainee_management.repositories.QuestionOptionRepository;
import propensi.tens.bms.features.trainee_management.services.AssessmentAnswerService;

@RestController
@RequestMapping("/api/trainee/answers")
public class AssessmentAnswerController {

    @Autowired
    private AssessmentRepository assessmentRepository;
    
    @Autowired
    private QuestionOptionRepository optionRepository;

    @Autowired
    private AssessmentAnswerService answerService;
    
    @GetMapping("/correct/{assessmentId}")
    public ResponseEntity<?> getCorrectAnswers(@PathVariable("assessmentId") Long assessmentId) {
        BaseResponseDTO<Map<Long, Long>> response = new BaseResponseDTO<>();
        try {
            Assessment assessment = assessmentRepository.findById(assessmentId)
                    .orElseThrow(() -> new IllegalArgumentException("Assessment tidak ditemukan dengan ID: " + assessmentId));
            
            Map<Long, Long> correctAnswers = new HashMap<>();
            
            for (AssessmentQuestion question : assessment.getQuestions()) {
                List<QuestionOption> correctOptions = optionRepository.findByQuestionIdAndIsCorrectTrue(question.getId());
                
                if (!correctOptions.isEmpty()) {
                    correctAnswers.put(question.getId(), correctOptions.get(0).getId());
                }
            }
            
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Jawaban benar berhasil diambil");
            response.setTimestamp(new Date());
            response.setData(correctAnswers);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            response.setMessage(e.getMessage());
            response.setTimestamp(new Date());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{submissionId}")
    public ResponseEntity<List<AnswerKeyValueDTO>> getAnswerKeyValues(
            @PathVariable("submissionId") Long submissionId) {
        List<AnswerKeyValueDTO> list = answerService.getKeyValueAnswersBySubmissionId(submissionId);
        if (list.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(list);
    }
}