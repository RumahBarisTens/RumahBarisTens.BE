package propensi.tens.bms.features.trainee_management.services;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import propensi.tens.bms.features.account_management.models.EndUser;
import propensi.tens.bms.features.account_management.repositories.EndUserDb;
import propensi.tens.bms.features.notification_management.services.NotificationService;
import propensi.tens.bms.features.trainee_management.dto.request.CreateAssessmentRequest;
import propensi.tens.bms.features.trainee_management.dto.request.UpdateAssessmentRequest;
import propensi.tens.bms.features.trainee_management.dto.response.AssessmentResponse;
import propensi.tens.bms.features.trainee_management.dto.response.QuestionDTO;
import propensi.tens.bms.features.trainee_management.dto.response.SubmissionSummaryDTO;
import propensi.tens.bms.features.trainee_management.dto.response.UserDTO;
import propensi.tens.bms.features.trainee_management.enums.QuestionType;
import propensi.tens.bms.features.trainee_management.models.Assessment;
import propensi.tens.bms.features.trainee_management.models.AssessmentQuestion;
import propensi.tens.bms.features.trainee_management.models.QuestionOption;
import propensi.tens.bms.features.trainee_management.repositories.AssessmentRepository;
import propensi.tens.bms.features.trainee_management.repositories.AssessmentSubmissionRepository;
import propensi.tens.bms.features.trainee_management.repositories.AssessmentQuestionRepository;

@Service
@Transactional
public class AssessmentServiceImpl implements AssessmentService {
    @Autowired
    private AssessmentRepository assessmentRepository;

    @Autowired
    private AssessmentQuestionRepository assessmentQuestionRepository;

    @Autowired
    private EndUserDb endUserDb;

    @Autowired
    private AssessmentSubmissionRepository submissionRepository;

    @Autowired private NotificationService notificationService;

    @Override
@Transactional
public AssessmentResponse createAssessment(CreateAssessmentRequest request) {
    Assessment assessment = new Assessment();
    assessment.setTemplate(request.getTemplate());
    assessment.setDeadline(request.getDeadline());
    Set<EndUser> users = new HashSet<>();
    for (String username : request.getAssignedUsername()) {
        users.add(endUserDb.findByUsername(username));
    }
    assessment.setAssignedUsers(users);
    
    // First save the assessment to get an ID
    Assessment saved = assessmentRepository.save(assessment);
    
    // Find template questions
    List<AssessmentQuestion> templateQuestions = assessmentQuestionRepository.findByTemplate(request.getTemplate());
    
    // For each template question, create a new question for this assessment with a UNIQUE code
    for (int i = 0; i < templateQuestions.size(); i++) {
        AssessmentQuestion templateQuestion = templateQuestions.get(i);
        AssessmentQuestion newQuestion = new AssessmentQuestion();
        
        // Generate a unique code using the assessment ID
        String uniqueCode = String.format("%s-A%d-Q%d", 
                                         request.getTemplate().toString().substring(0, 2),
                                         saved.getId(),
                                         i + 1);
        
        newQuestion.setCode(uniqueCode); // Use the unique code instead of copying the template code
        newQuestion.setQuestionType(templateQuestion.getQuestionType());
        newQuestion.setTemplate(templateQuestion.getTemplate());
        newQuestion.setQuestionText(templateQuestion.getQuestionText());
        newQuestion.setAssessment(saved);
        
        // Copy options if it's a multiple choice question
        if (templateQuestion.getQuestionType() == QuestionType.MULTIPLE_CHOICE) {
            for (QuestionOption templateOption : templateQuestion.getOptions()) {
                QuestionOption newOption = new QuestionOption();
                newOption.setOptionText(templateOption.getOptionText());
                newOption.setIsCorrect(templateOption.getIsCorrect());
                newOption.setQuestion(newQuestion);
                newQuestion.getOptions().add(newOption);
            }
        }
        
        saved.getQuestions().add(newQuestion);
    }
    
    // Save the assessment again with questions
    saved = assessmentRepository.save(saved);
    notificationService.notifyAssessmentAssigned(saved.getId(), users);
    
    return mapToResponse(saved);
}

    @Override
    public List<AssessmentResponse> getAllAssessments() {
        return assessmentRepository.findAll()
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Override
    public AssessmentResponse getAssessmentById(Long id) {
        Assessment assessment = assessmentRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Assessment not found: " + id));
        return mapToResponse(assessment);
    }

    @Override
    public AssessmentResponse updateAssessment(Long id, UpdateAssessmentRequest request) {
        Assessment assessment = assessmentRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Assessment not found: " + id));
        assessment.setDeadline(request.getDeadline());
        Set<EndUser> users = new HashSet<>();
        for (String username : request.getAssignedUsername()) {
            users.add(endUserDb.findByUsername(username));
        }
        assessment.setAssignedUsers(users);
        Assessment updated = assessmentRepository.save(assessment);
        return mapToResponse(updated);
    }

    @Override
    public void deleteAssessment(Long id) {
        assessmentRepository.deleteById(id);
    }

    private AssessmentResponse mapToResponse(Assessment a) {
        List<UserDTO> assigned = a.getAssignedUsers()
            .stream()
            .map(u -> new UserDTO(u.getId(), u.getFullName()))
            .collect(Collectors.toList());
        
        List<QuestionDTO> questions = a.getQuestions().stream()
            .map(q -> {
                // Ubah dari List<String> menjadi List<Map<String, String>>
                List<Map<String, String>> optionMaps = q.getOptions().stream()
                    .map(opt -> {
                        Map<String, String> optionMap = new HashMap<>();
                        optionMap.put("id", opt.getId().toString());
                        optionMap.put("text", opt.getOptionText());
                        return optionMap;
                    })
                    .collect(Collectors.toList());
                
                return new QuestionDTO(
                    q.getId().toString(),
                    q.getQuestionText(),
                    q.getQuestionType(),
                    optionMaps
                );
            })
            .collect(Collectors.toList());
            
        return new AssessmentResponse(a.getId(), a.getTemplate(), a.getDeadline(), assigned, questions);
    }
        
    @Override
    public List<String> getAllBaristaUsernames() {
        List<EndUser> baristas = assessmentRepository.findAllBarista();
        return baristas.stream()
            .map(EndUser::getUsername)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<String> getAllHeadBarUsernames() {
        List<EndUser> headbars = assessmentRepository.findAllHeadbar();
        return headbars.stream()
            .map(EndUser::getUsername)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<String> getAllTraineeBaristaUsernames() {
        List<EndUser> trainees = assessmentRepository.findAllTraineeBarista();
        return trainees.stream()
            .map(EndUser::getUsername)
            .collect(Collectors.toList());
    }

    @Override
    public List<AssessmentResponse> getAssessmentsByUserId(String username) {
        EndUser user = endUserDb.findByUsername(username);
        return assessmentRepository
            .findByUserId(user.getId())
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<SubmissionSummaryDTO> getAssessmentResults(Long assessmentId, String outlet) {
        List<SubmissionSummaryDTO> results = submissionRepository.findSummariesByAssessmentId(assessmentId);
        
        if (outlet != null) {
            results = results.stream()
                .filter(r -> {
                    // Asumsikan outlet info ada di username (contoh: "john-outletA")
                    // Atau tambahkan field outlet di SubmissionSummaryDTO jika perlu
                    return r.getUsername().contains(outlet);
                })
                .collect(Collectors.toList());
        }
        return results;
    }

    @Override
    public Map<String, Object> getAssessmentDashboardData(Long assessmentId) {
        Map<String, Object> dashboardData = new HashMap<>();
        
        // 1. Data summary
        dashboardData.put("averageScore", submissionRepository.calculateAverageScoreByAssessmentId(assessmentId));
        dashboardData.put("scoreDistribution", submissionRepository.getScoreDistributionByAssessmentId(assessmentId));
        
        // 2. List peserta
        dashboardData.put("participants", submissionRepository.findSummariesByAssessmentId(assessmentId));
        
        return dashboardData;
    }
}