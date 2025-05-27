package propensi.tens.bms.features.trainee_management.models;

import jakarta.persistence.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Entity
@Table(name = "question_options")
public class QuestionOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "option_id")
    private Long id;

    // ← Each option belongs to exactly one question
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private AssessmentQuestion question;

    @Column(name = "option_text", columnDefinition = "TEXT", nullable = false)
    private String optionText;

    @Column(name = "is_correct", nullable = false)
    private Boolean isCorrect;
}
