package propensi.tens.bms.features.trainee_management.services;

import java.util.List;
import propensi.tens.bms.features.trainee_management.dto.request.CreatePeerReviewSubmissionRequest;
import propensi.tens.bms.features.trainee_management.dto.response.PeerReviewSubmissionResponse;
import propensi.tens.bms.features.trainee_management.models.PeerReviewSubmission;

public interface PeerReviewSubmissionService {
    PeerReviewSubmissionResponse submit(CreatePeerReviewSubmissionRequest req);
    List<PeerReviewSubmissionResponse> getByReviewer(String reviewerUsername);
    // List<PeerReviewSubmission> getPeerReviewHistoryByUsername(String username);

}
    
