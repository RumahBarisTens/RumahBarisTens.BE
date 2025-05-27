package propensi.tens.bms.features.trainee_management.dto.response;

import java.time.LocalDate;
import java.util.List;

import propensi.tens.bms.features.trainee_management.enums.AssessmentTemplate;

public class AssessmentResponse {
    private Long id;
    private AssessmentTemplate template;
    private LocalDate deadline;
    private List<UserDTO> assignedUsername;
    private List<QuestionDTO> questions;

    public AssessmentResponse(Long id, AssessmentTemplate template, LocalDate deadline,
                              List<UserDTO> assignedUsername, List<QuestionDTO> questions) {
        this.id = id;
        this.template = template;
        this.deadline = deadline;
        this.assignedUsername = assignedUsername;
        this.questions = questions;
    }
    public Long getId() { return id; }
    public AssessmentTemplate getTemplate() { return template; }
    public LocalDate getDeadline() { return deadline; }
    public List<UserDTO> getAssignedUsers() { return assignedUsername; }
    public List<QuestionDTO> getQuestions() { return questions; }
}
