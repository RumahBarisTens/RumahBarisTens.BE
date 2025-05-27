package propensi.tens.bms.features.trainee_management.models;

import jakarta.persistence.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Entity
@Table(name = "assessment_answers")
public class AssessmentAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Parent submission */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "submission_id")
    private AssessmentSubmission submission;

    /** Soal */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id")
    private AssessmentQuestion question;

    /** Kalau MC: id opsi yang dipilih; kalau essay, null */
    @Column(name = "selected_option_id")
    private Long selectedOptionId;

    /** Kalau essay: teks jawaban; kalau MC, null */
    @Column(name = "essay_answer", columnDefinition = "TEXT")
    private String essayAnswer;

    /** Skor per‐essay (ditentukan evaluator), MC dikosongi */
    @Column(name = "essay_raw_score")
    private Double essayRawScore;
}
