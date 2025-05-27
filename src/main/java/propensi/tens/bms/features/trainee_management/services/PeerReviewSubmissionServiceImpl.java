package propensi.tens.bms.features.trainee_management.services;

import java.util.Date;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import propensi.tens.bms.features.trainee_management.models.*;
import propensi.tens.bms.features.trainee_management.repositories.*;
import propensi.tens.bms.features.trainee_management.dto.request.CreatePeerReviewSubmissionRequest;
import propensi.tens.bms.features.trainee_management.dto.response.PeerReviewSubmissionResponse;

@Service @RequiredArgsConstructor
public class PeerReviewSubmissionServiceImpl implements PeerReviewSubmissionService {
    private final PeerReviewSubmissionRepository subRepo;
    private final PeerReviewAssignmentRepository assignRepo;

    @Override
    @Transactional
    public PeerReviewSubmissionResponse submit(CreatePeerReviewSubmissionRequest req) {
        PeerReviewAssignment assignment = assignRepo.findById(req.getAssignmentId())
            .orElseThrow(() -> new RuntimeException("Assignment not found"));
        PeerReviewSubmission s = new PeerReviewSubmission();
        s.setAssignment(assignment);
        s.setReviewedAt(new Date());
        s.setQ1(req.getQ1()); s.setQ2(req.getQ2()); s.setQ3(req.getQ3());
        s.setQ4(req.getQ4()); s.setQ5(req.getQ5()); s.setQ6(req.getQ6());
        s.setQ7(req.getQ7()); s.setQ8(req.getQ8()); s.setQ9(req.getQ9());
        s.setQ10(req.getQ10());
        PeerReviewSubmission saved = subRepo.save(s);
        assignment.setReviewedAt(new Date());
        assignRepo.save(assignment);
        return new PeerReviewSubmissionResponse(
            saved.getId(),
            assignment.getPeerReviewAssignmentId(),
            assignment.getReviewer().getUsername(),
            assignment.getReviewee().getUsername(),
            saved.getReviewedAt(),
            saved.getQ1(),saved.getQ2(),saved.getQ3(),saved.getQ4(),saved.getQ5(),
            saved.getQ6(),saved.getQ7(),saved.getQ8(),saved.getQ9(),saved.getQ10()
        );
    }

    @Override
    public List<PeerReviewSubmissionResponse> getByReviewer(String reviewerUsername) {
        return subRepo.findByAssignmentReviewerUsername(reviewerUsername).stream().map(s ->
            new PeerReviewSubmissionResponse(
                s.getId(),
                s.getAssignment().getPeerReviewAssignmentId(),
                reviewerUsername,
                s.getAssignment().getReviewee().getUsername(),
                s.getReviewedAt(),
                s.getQ1(),s.getQ2(),s.getQ3(),s.getQ4(),s.getQ5(),
                s.getQ6(),s.getQ7(),s.getQ8(),s.getQ9(),s.getQ10()
            )
        ).toList();
    }
}
