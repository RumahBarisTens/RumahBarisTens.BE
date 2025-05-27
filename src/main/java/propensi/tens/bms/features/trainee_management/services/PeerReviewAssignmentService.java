package propensi.tens.bms.features.trainee_management.services;

import java.util.List;
import propensi.tens.bms.features.trainee_management.dto.request.PeerReviewAssignmentRequestDTO;
import propensi.tens.bms.features.trainee_management.dto.request.UpdatePeerReviewAssignmentRequestDTO;
import propensi.tens.bms.features.trainee_management.dto.response.PeerReviewAssignmentResponseDTO;

public interface PeerReviewAssignmentService {
    PeerReviewAssignmentResponseDTO createPeerReviewAssignment(PeerReviewAssignmentRequestDTO request) throws Exception;
    List<PeerReviewAssignmentResponseDTO> getAllPeerReviewAssignments();
    PeerReviewAssignmentResponseDTO getPeerReviewAssignmentById(Integer id) throws Exception;
    List<String> getAllReviewerUsernamesExceptAdmin();
    List<String> getAllProbationBaristaUsernames();
    
    // Method untuk mendapatkan semua peer review assignment berdasarkan reviewee
    List<PeerReviewAssignmentResponseDTO> getPeerReviewAssignmentsByReviewee(String revieweeUsername) throws Exception;

    List<PeerReviewAssignmentResponseDTO> getPeerReviewAssignmentsByReviewer(String reviewerUsername) throws Exception;
    
    // Method untuk update peer review assignment
    List<PeerReviewAssignmentResponseDTO> updatePeerReviewAssignments(String revieweeUsername, UpdatePeerReviewAssignmentRequestDTO request) throws Exception;
}
