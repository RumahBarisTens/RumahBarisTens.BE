package propensi.tens.bms.features.trainee_management.models;

import jakarta.persistence.*;
import lombok.*;
import propensi.tens.bms.features.account_management.models.EndUser;

import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Entity
@Table(name = "assessment_submissions")
public class AssessmentSubmission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "assessment_id")
    private Assessment assessment;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private EndUser user;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date submittedAt;

    /** skor dari semua MC (otomatis) */
    @Column(name = "mc_score", nullable = false)
    private Double mcScore;

    /** skor dari essay (setelah direview) */
    @Column(name = "essay_score")
    private Double essayScore;

    /** totalScore = mcScore + essayScore (essayScore dipakai 0 kalau belum direview) */
    @Column(name = "total_score", nullable = false)
    private Double totalScore;

    /** apakah essay sudah dinilai oleh evaluator */
    @Column(name = "essay_reviewed", nullable = false)
    private Boolean essayReviewed = false;

    @OneToMany(
      mappedBy = "submission",
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = FetchType.LAZY
    )
    private Set<AssessmentAnswer> answers = new HashSet<>();
}
