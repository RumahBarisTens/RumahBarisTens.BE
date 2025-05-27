package propensi.tens.bms.features.trainee_management.models;

import jakarta.persistence.*;
import lombok.*;
import propensi.tens.bms.features.account_management.models.EndUser;
import propensi.tens.bms.features.trainee_management.enums.AssessmentTemplate;

import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.util.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Entity
@Table(name = "assessments")
public class Assessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "assessment_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssessmentTemplate template;

    @Column(name = "deadline", nullable = false)
    private LocalDate deadline;

    @OneToMany(
        mappedBy = "assessment",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    private Set<AssessmentQuestion> questions = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "assessment_assignments",
        joinColumns = @JoinColumn(name = "assessment_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<EndUser> assignedUsers = new HashSet<>();

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false, updatable = false)
    private Date createdAt;
}
