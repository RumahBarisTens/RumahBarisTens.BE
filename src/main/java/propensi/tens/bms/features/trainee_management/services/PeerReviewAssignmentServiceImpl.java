package propensi.tens.bms.features.trainee_management.services;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import propensi.tens.bms.features.account_management.models.EndUser;
import propensi.tens.bms.features.account_management.repositories.EndUserDb;
import propensi.tens.bms.features.notification_management.services.NotificationService;
import propensi.tens.bms.features.trainee_management.dto.request.PeerReviewAssignmentRequestDTO;
import propensi.tens.bms.features.trainee_management.dto.request.UpdatePeerReviewAssignmentRequestDTO;
import propensi.tens.bms.features.trainee_management.dto.response.PeerReviewAssignmentResponseDTO;
import propensi.tens.bms.features.trainee_management.models.PeerReviewAssignment;
import propensi.tens.bms.features.trainee_management.repositories.PeerReviewAssignmentRepository;

@Service
public class PeerReviewAssignmentServiceImpl implements PeerReviewAssignmentService {

    @Autowired
    private PeerReviewAssignmentRepository peerReviewAssignmentRepository;

    @Autowired
    private EndUserDb endUserDb;

    @Autowired private NotificationService notificationService;

    @Override
    public PeerReviewAssignmentResponseDTO createPeerReviewAssignment(PeerReviewAssignmentRequestDTO request) throws Exception {
        EndUser reviewer = endUserDb.findByUsername(request.getReviewerUsername());
        EndUser reviewee = endUserDb.findByUsername(request.getRevieweeUsername());
        if (reviewer.getId().equals(reviewee.getId())) {
            throw new Exception("Reviewer and reviewee cannot be the same user");
        }
        PeerReviewAssignment assignment = new PeerReviewAssignment();
        assignment.setReviewer(reviewer);
        assignment.setReviewee(reviewee);
        assignment.setEndDateFill(request.getEndDateFill());
        PeerReviewAssignment saved = peerReviewAssignmentRepository.save(assignment);
        PeerReviewAssignmentResponseDTO response = new PeerReviewAssignmentResponseDTO();
        response.setPeerReviewAssignmentId(saved.getPeerReviewAssignmentId());
        response.setReviewerUsername(saved.getReviewer().getUsername());
        response.setRevieweeUsername(saved.getReviewee().getUsername());
        response.setEndDateFill(saved.getEndDateFill());
        notificationService.notifyPeerReviewAssigned(saved.getPeerReviewAssignmentId(), reviewer.getUsername(), reviewee.getUsername());
        return response;
    }

    @Override
    public List<PeerReviewAssignmentResponseDTO> getAllPeerReviewAssignments() {
        List<PeerReviewAssignment> list = peerReviewAssignmentRepository.findAll();
        List<PeerReviewAssignmentResponseDTO> dtoList = new ArrayList<>();
        for (PeerReviewAssignment assignment : list) {
            PeerReviewAssignmentResponseDTO dto = new PeerReviewAssignmentResponseDTO();
            dto.setPeerReviewAssignmentId(assignment.getPeerReviewAssignmentId());
            dto.setReviewerUsername(assignment.getReviewer().getUsername());
            dto.setRevieweeUsername(assignment.getReviewee().getUsername());
            dto.setEndDateFill(assignment.getEndDateFill());
            dtoList.add(dto);
        }
        return dtoList;
    }

    @Override
    public PeerReviewAssignmentResponseDTO getPeerReviewAssignmentById(Integer id) throws Exception {
        PeerReviewAssignment assignment = peerReviewAssignmentRepository.findById(id)
                .orElseThrow(() -> new Exception("Peer review assignment not found with id: " + id));
        PeerReviewAssignmentResponseDTO dto = new PeerReviewAssignmentResponseDTO();
        dto.setPeerReviewAssignmentId(assignment.getPeerReviewAssignmentId());
        dto.setReviewerUsername(assignment.getReviewer().getUsername());
        dto.setRevieweeUsername(assignment.getReviewee().getUsername());
        dto.setEndDateFill(assignment.getEndDateFill());
        return dto;
    }

    // Method untuk menampilkan seluruh reviewer (selain Admin)
    @Override
    public List<String> getAllReviewerUsernamesExceptAdmin() {
        List<EndUser> users = peerReviewAssignmentRepository.findAllExceptAdmin();
        return users.stream().map(EndUser::getUsername).collect(Collectors.toList());
    }

    // Method untuk menampilkan seluruh probation barista (reviewee)
    @Override
    public List<String> getAllProbationBaristaUsernames() {
        List<EndUser> probationUsers = peerReviewAssignmentRepository.findAllProbationBarista();
        return probationUsers.stream().map(EndUser::getUsername).collect(Collectors.toList());
    }

    @Override
    public List<PeerReviewAssignmentResponseDTO> getPeerReviewAssignmentsByReviewee(String revieweeUsername) throws Exception {
        EndUser reviewee = endUserDb.findByUsername(revieweeUsername);
        if (reviewee == null) {
            throw new Exception("Reviewee dengan username " + revieweeUsername + " tidak ditemukan");
        }
        
        List<PeerReviewAssignment> assignments = peerReviewAssignmentRepository.findByRevieweeAndReviewedAtIsNull(reviewee);
        
        List<PeerReviewAssignmentResponseDTO> responseList = new ArrayList<>();
        for (PeerReviewAssignment assignment : assignments) {
            PeerReviewAssignmentResponseDTO response = new PeerReviewAssignmentResponseDTO();
            response.setPeerReviewAssignmentId(assignment.getPeerReviewAssignmentId());
            response.setReviewerUsername(assignment.getReviewer().getUsername());
            response.setRevieweeUsername(assignment.getReviewee().getUsername());
            response.setEndDateFill(assignment.getEndDateFill());
            responseList.add(response);
        }
        
        return responseList;
    }

    @Override
    public List<PeerReviewAssignmentResponseDTO> getPeerReviewAssignmentsByReviewer(String reviewerUsername) throws Exception {
        EndUser reviewer = endUserDb.findByUsername(reviewerUsername);
        if (reviewer == null) {
            throw new Exception("Reviewee dengan username " + reviewerUsername + " tidak ditemukan");
        }
        
        List<PeerReviewAssignment> assignments = peerReviewAssignmentRepository.findByReviewerAndReviewedAtIsNull(reviewer);
        
        List<PeerReviewAssignmentResponseDTO> responseList = new ArrayList<>();
        for (PeerReviewAssignment assignment : assignments) {
            PeerReviewAssignmentResponseDTO response = new PeerReviewAssignmentResponseDTO();
            response.setPeerReviewAssignmentId(assignment.getPeerReviewAssignmentId());
            response.setReviewerUsername(assignment.getReviewer().getUsername());
            response.setRevieweeUsername(assignment.getReviewee().getUsername());
            response.setEndDateFill(assignment.getEndDateFill());
            responseList.add(response);
        }
        
        return responseList;
    }

    @Override
    @Transactional
    public List<PeerReviewAssignmentResponseDTO> updatePeerReviewAssignments(String revieweeUsername, UpdatePeerReviewAssignmentRequestDTO request) throws Exception {
        // Cari reviewee berdasarkan username
        EndUser reviewee = endUserDb.findByUsername(revieweeUsername);
        if (reviewee == null) {
            throw new Exception("Reviewee dengan username " + revieweeUsername + " tidak ditemukan");
        }
        
        List<PeerReviewAssignment> existingAssignments = peerReviewAssignmentRepository.findByRevieweeAndReviewedAtIsNull(reviewee);
        
        peerReviewAssignmentRepository.deleteAll(existingAssignments);
        
        List<PeerReviewAssignment> newAssignments = new ArrayList<>();
        for (String reviewerUsername : request.getReviewerUsernames()) {
            EndUser reviewer = endUserDb.findByUsername(reviewerUsername);
            if (reviewer == null) {
                throw new Exception("Reviewer dengan username " + reviewerUsername + " tidak ditemukan");
            }
            
            if (reviewer.getId().equals(reviewee.getId())) {
                throw new Exception("Reviewer dan reviewee tidak boleh sama");
            }
            
            // Buat assignment baru
            PeerReviewAssignment newAssignment = new PeerReviewAssignment();
            newAssignment.setReviewer(reviewer);
            newAssignment.setReviewee(reviewee);
            newAssignment.setEndDateFill(request.getEndDateFill());
            
            newAssignments.add(newAssignment);
        }
        
        // Simpan semua assignment baru
        List<PeerReviewAssignment> savedAssignments = peerReviewAssignmentRepository.saveAll(newAssignments);
        
        // Konversi ke response DTO
        List<PeerReviewAssignmentResponseDTO> responseList = new ArrayList<>();
        for (PeerReviewAssignment assignment : savedAssignments) {
            PeerReviewAssignmentResponseDTO response = new PeerReviewAssignmentResponseDTO();
            response.setPeerReviewAssignmentId(assignment.getPeerReviewAssignmentId());
            response.setReviewerUsername(assignment.getReviewer().getUsername());
            response.setRevieweeUsername(assignment.getReviewee().getUsername());
            response.setEndDateFill(assignment.getEndDateFill());
            responseList.add(response);
        }
        
        return responseList;
    }
}
