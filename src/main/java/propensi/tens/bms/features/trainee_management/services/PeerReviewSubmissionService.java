package propensi.tens.bms.features.trainee_management.services;

import java.util.List;
import propensi.tens.bms.features.trainee_management.dto.request.CreatePeerReviewSubmissionRequest;
import propensi.tens.bms.features.trainee_management.dto.response.PeerReviewSubmissionResponse;

public interface PeerReviewSubmissionService {
    PeerReviewSubmissionResponse submit(CreatePeerReviewSubmissionRequest req);
    List<PeerReviewSubmissionResponse> getByReviewer(String reviewerUsername);
}
