package propensi.tens.bms.features.trainee_management.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

import propensi.tens.bms.features.account_management.models.EndUser;
import propensi.tens.bms.features.trainee_management.enums.AssessmentTemplate;
import propensi.tens.bms.features.trainee_management.models.Assessment;

public interface AssessmentRepository extends JpaRepository<Assessment, Long> {
    List<Assessment> findByTemplate(AssessmentTemplate template);

    @Query("SELECT e FROM EndUser e WHERE TYPE(e) = Barista")
    List<EndUser> findAllBarista();

    @Query("SELECT e FROM EndUser e WHERE TYPE(e) = HeadBar")
    List<EndUser> findAllHeadbar();

    @Query("SELECT e FROM EndUser e WHERE TYPE(e) = Barista AND TREAT(e AS Barista).isIntern = true")
    List<EndUser> findAllTraineeBarista();

    @Query("""
        select a
          from Assessment a
          join a.assignedUsers u
         where u.id = :userId
           and not exists (
               select 1
               from AssessmentSubmission s
               where s.assessment.id = a.id
                 and s.user.id = :userId
           )
    """)
    List<Assessment> findByUserId(@Param("userId") UUID userId);
}
