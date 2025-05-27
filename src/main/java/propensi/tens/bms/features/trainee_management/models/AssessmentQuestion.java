package propensi.tens.bms.features.trainee_management.models;

import jakarta.persistence.*;
import lombok.*;
import propensi.tens.bms.features.trainee_management.enums.AssessmentTemplate;
import propensi.tens.bms.features.trainee_management.enums.QuestionType;

import java.util.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Entity
@Table(name = "assessment_questions")
public class AssessmentQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private Long id;

    @Column(name = "question_code", nullable = false, unique = true)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuestionType questionType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssessmentTemplate template;

    @Column(name = "question_text", columnDefinition = "TEXT", nullable = false)
    private String questionText;

    // ← NEW: many questions → one assessment
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assessment_id", nullable = false)
    private Assessment assessment;

    // ← Already was, but showing for completeness
    @OneToMany(
        mappedBy = "question",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.EAGER
    )
    private Set<QuestionOption> options = new HashSet<>();
}
