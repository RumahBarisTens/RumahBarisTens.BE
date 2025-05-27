package propensi.tens.bms.features.trainee_management.models;

import java.util.*;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;

import lombok.*;
import propensi.tens.bms.features.trainee_management.enums.AssignedRole;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "training_material")
public class TrainingMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "training_material_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String link;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ElementCollection(targetClass = AssignedRole.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "training_material_roles", joinColumns = @JoinColumn(name = "training_material_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "assigned_role")
    private Set<AssignedRole> assignedRoles = new HashSet<>();

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false, updatable = false)
    private Date createdAt;
}
