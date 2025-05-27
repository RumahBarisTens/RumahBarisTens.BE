package propensi.tens.bms.features.trainee_management.services;

import java.util.List;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import propensi.tens.bms.features.trainee_management.dto.request.SubmitAssessmentRequestDTO;
import propensi.tens.bms.features.trainee_management.dto.request.EssayReviewRequestDTO;
import propensi.tens.bms.features.trainee_management.dto.response.SubmissionSummaryDTO;
import propensi.tens.bms.features.trainee_management.models.*;
import propensi.tens.bms.features.trainee_management.repositories.*;
import propensi.tens.bms.features.account_management.models.EndUser;
import propensi.tens.bms.features.account_management.repositories.EndUserDb;
import propensi.tens.bms.features.notification_management.services.NotificationService;
import propensi.tens.bms.features.trainee_management.enums.QuestionType;

import java.util.Date;

@Service
public class AssessmentSubmissionServiceImpl implements AssessmentSubmissionService {
    
    @Autowired private EndUserDb endUserDb;
    @Autowired private AssessmentRepository assessmentRepo;
    @Autowired private AssessmentQuestionRepository questionRepo;
    @Autowired private QuestionOptionRepository optionRepo;
    @Autowired private AssessmentSubmissionRepository submissionRepo;
    @Autowired private NotificationService notificationService;
    
    @Override
    @Transactional
    public void submitAssessment(SubmitAssessmentRequestDTO dto) {
        String username = dto.getUsername();
        EndUser user = endUserDb.findByUsername(username);
        if (user == null) {
            throw new EntityNotFoundException("User not found: " + username);
        }
    
        Assessment assessment = assessmentRepo.findById(dto.getAssessmentId())
            .orElseThrow(() -> new EntityNotFoundException(
                "Assessment not found: " + dto.getAssessmentId()));
    
        // Buat submission baru
        AssessmentSubmission submission = new AssessmentSubmission();
        submission.setAssessment(assessment);
        submission.setUser(user);
        submission.setSubmittedAt(new Date());
        submission.setMcScore(0.0);
        submission.setEssayScore(0.0);
        submission.setTotalScore(0.0);
        submission.setEssayReviewed(false);
        submission = submissionRepo.save(submission);
    
        // Hitung bobot MC
        List<AssessmentQuestion> questions = questionRepo.findByAssessmentId(assessment.getId());
        long mcCount = questions.stream()
            .filter(q -> q.getQuestionType() == QuestionType.MULTIPLE_CHOICE)
            .count();
        double perMcWeight = mcCount > 0 ? 75.0 / mcCount : 0.0;
        double mcScore = 0.0;
    
        // Proses setiap jawaban
        for (SubmitAssessmentRequestDTO.QuestionAnswerDTO ansDto : dto.getAnswers()) {
            AssessmentQuestion question = questionRepo.findById(ansDto.getQuestionId())
                .orElseThrow(() -> new EntityNotFoundException(
                    "Question not found: " + ansDto.getQuestionId()));
    
            AssessmentAnswer answer = new AssessmentAnswer();
            answer.setSubmission(submission);
            answer.setQuestion(question);
    
            if (question.getQuestionType() == QuestionType.MULTIPLE_CHOICE) {
                // ----- MODIFIKASI UNTUK MC -----
                Long selectedOptionId = ansDto.getSelectedOptionId();
                answer.setSelectedOptionId(selectedOptionId);
    
                if (selectedOptionId != null && selectedOptionId != -1L) {
                    // baru lookup dan cek kebenaran kalau id valid
                    QuestionOption picked = optionRepo.findById(selectedOptionId)
                        .orElseThrow(() -> new EntityNotFoundException(
                            "Option not found: " + selectedOptionId));
                    if (picked.getIsCorrect()) {
                        mcScore += perMcWeight;
                    }
                }
    
            } else {
                // essay
                answer.setEssayAnswer(ansDto.getEssayAnswer());
            }
    
            submission.getAnswers().add(answer);
        }
    
        // Simpan skor
        submission.setMcScore(mcScore);
        submission.setTotalScore(mcScore);
        submissionRepo.save(submission);
        notificationService.notifyEssayReviewRequired(submission.getId(), username);
    }
    

    @Override
    @Transactional(readOnly = true)
    public List<SubmissionSummaryDTO> getSubmissionSummariesByAssessmentId(Long assessmentId) {
        if (!assessmentRepo.existsById(assessmentId)) {
            throw new EntityNotFoundException("Assessment not found: " + assessmentId);
        }
        return submissionRepo.findSummariesByAssessmentId(assessmentId);
    }
    
    @Override
    @Transactional
    public void reviewEssaySubmission(EssayReviewRequestDTO dto) {
        // Validasi input
        if (dto.getSubmissionId() == null) {
            throw new IllegalArgumentException("ID submission tidak boleh kosong");
        }
        
        if (dto.getEssayScore() < 0 || dto.getEssayScore() > 25) {
            throw new IllegalArgumentException("Nilai essay harus antara 0-25");
        }
        
        // Cari submission berdasarkan ID
        AssessmentSubmission submission = submissionRepo.findById(dto.getSubmissionId())
            .orElseThrow(() -> new EntityNotFoundException("Submission tidak ditemukan dengan ID: " + dto.getSubmissionId()));
        
        // Update nilai essay dan total score
        submission.setEssayScore(dto.getEssayScore());
        submission.setTotalScore(submission.getMcScore() + dto.getEssayScore());
        submission.setEssayReviewed(true);
        
        // Simpan perubahan
        submissionRepo.save(submission);
    }


    @Override
    public List<SubmissionSummaryDTO> getUserSubmissions(String username) {
        var user = endUserDb.findByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("User with username " + username + " not found");
        }
        
        // log.info("Fetching assessment submission history for user: {}", username);
        return submissionRepo.findAllByUserIdOrderBySubmittedAtDesc(user.getId());
    }
}
