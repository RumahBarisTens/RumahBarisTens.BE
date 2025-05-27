package propensi.tens.bms.features.trainee_management.models;

import java.util.Date;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "peer_review_submission")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class PeerReviewSubmission {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "assignment_id", nullable = false)
    private PeerReviewAssignment assignment;

    @Column(name = "reviewed_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date reviewedAt;

    // skor soal 1–10, skala dinamis
    @Column(name = "q1") private Double q1;
    @Column(name = "q2") private Double q2;
    @Column(name = "q3") private Double q3;
    @Column(name = "q4") private Double q4;
    @Column(name = "q5") private Double q5;
    @Column(name = "q6") private Double q6;
    @Column(name = "q7") private Double q7;
    @Column(name = "q8") private Double q8;
    @Column(name = "q9") private Double q9;
    @Column(name = "q10") private Double q10;
}
