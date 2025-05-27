package propensi.tens.bms.features.trainee_management.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "peer_review_content")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class PeerReviewContent {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "question_number", nullable = false, unique = true)
    private Integer questionNumber;

    @Column(nullable = false)
    private String text;
}
