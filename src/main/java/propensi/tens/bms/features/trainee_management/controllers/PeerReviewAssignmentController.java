package propensi.tens.bms.features.trainee_management.controllers;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import propensi.tens.bms.features.account_management.dto.response.BaseResponseDTO;
import propensi.tens.bms.features.trainee_management.dto.request.PeerReviewAssignmentRequestDTO;
import propensi.tens.bms.features.trainee_management.dto.request.UpdatePeerReviewAssignmentRequestDTO;
import propensi.tens.bms.features.trainee_management.dto.response.PeerReviewAssignmentResponseDTO;
import propensi.tens.bms.features.trainee_management.services.PeerReviewAssignmentService;

@RestController
@RequestMapping("/api/trainee/peer-review-assignment")
public class PeerReviewAssignmentController {

    @Autowired
    private PeerReviewAssignmentService peerReviewAssignmentService;

    @PostMapping("/create")
    public ResponseEntity<?> createPeerReviewAssignment(@RequestBody PeerReviewAssignmentRequestDTO request) {
        BaseResponseDTO<PeerReviewAssignmentResponseDTO> response = new BaseResponseDTO<>();
        try {
            PeerReviewAssignmentResponseDTO data = peerReviewAssignmentService.createPeerReviewAssignment(request);
            response.setStatus(HttpStatus.CREATED.value());
            response.setMessage("Peer review assignment created successfully");
            response.setTimestamp(new Date());
            response.setData(data);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage(e.getMessage());
            response.setTimestamp(new Date());
            response.setData(null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllPeerReviewAssignments() {
        BaseResponseDTO<List<PeerReviewAssignmentResponseDTO>> response = new BaseResponseDTO<>();
        try {
            List<PeerReviewAssignmentResponseDTO> list = peerReviewAssignmentService.getAllPeerReviewAssignments();
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("All peer review assignments retrieved");
            response.setTimestamp(new Date());
            response.setData(list);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage(e.getMessage());
            response.setTimestamp(new Date());
            response.setData(null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPeerReviewAssignmentById(@PathVariable("id") Integer id) {
        BaseResponseDTO<PeerReviewAssignmentResponseDTO> response = new BaseResponseDTO<>();
        try {
            PeerReviewAssignmentResponseDTO data = peerReviewAssignmentService.getPeerReviewAssignmentById(id);
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Peer review assignment retrieved successfully");
            response.setTimestamp(new Date());
            response.setData(data);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            response.setMessage(e.getMessage());
            response.setTimestamp(new Date());
            response.setData(null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    // Endpoint untuk mendapatkan list reviewer (selain Admin)
    @GetMapping("/reviewers")
    public ResponseEntity<?> getAllReviewers() {
        BaseResponseDTO<List<String>> response = new BaseResponseDTO<>();
        try {
            List<String> reviewers = peerReviewAssignmentService.getAllReviewerUsernamesExceptAdmin();
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("List of possible reviewers (excluding Admin) retrieved successfully");
            response.setTimestamp(new Date());
            response.setData(reviewers);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage(e.getMessage());
            response.setTimestamp(new Date());
            response.setData(null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint untuk mendapatkan list probation barista (reviewee)
    @GetMapping("/reviewees")
    public ResponseEntity<?> getAllReviewees() {
        BaseResponseDTO<List<String>> response = new BaseResponseDTO<>();
        try {
            List<String> reviewees = peerReviewAssignmentService.getAllProbationBaristaUsernames();
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("List of probation baristas retrieved successfully");
            response.setTimestamp(new Date());
            response.setData(reviewees);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage(e.getMessage());
            response.setTimestamp(new Date());
            response.setData(null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/by-reviewee/{revieweeUsername}")
    public ResponseEntity<?> getPeerReviewAssignmentsByReviewee(@PathVariable("revieweeUsername") String revieweeUsername) {
        BaseResponseDTO<List<PeerReviewAssignmentResponseDTO>> response = new BaseResponseDTO<>();
        try {
            List<PeerReviewAssignmentResponseDTO> list = peerReviewAssignmentService.getPeerReviewAssignmentsByReviewee(revieweeUsername);
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Peer review assignments untuk reviewee " + revieweeUsername + " berhasil diambil");
            response.setTimestamp(new Date());
            response.setData(list);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            response.setMessage(e.getMessage());
            response.setTimestamp(new Date());
            response.setData(null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/by-reviewer/{reviewerUsername}")
    public ResponseEntity<?> getPeerReviewAssignmentsByReviewer(@PathVariable("reviewerUsername") String reviewerUsername) {
        BaseResponseDTO<List<PeerReviewAssignmentResponseDTO>> response = new BaseResponseDTO<>();
        try {
            List<PeerReviewAssignmentResponseDTO> list = peerReviewAssignmentService.getPeerReviewAssignmentsByReviewer(reviewerUsername);
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Peer review assignments untuk reviewer " + reviewerUsername + " berhasil diambil");
            response.setTimestamp(new Date());
            response.setData(list);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            response.setMessage(e.getMessage());
            response.setTimestamp(new Date());
            response.setData(null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    // Endpoint untuk update peer review assignments
    @PutMapping("/update/{revieweeUsername}")
    public ResponseEntity<?> updatePeerReviewAssignments(
            @PathVariable("revieweeUsername") String revieweeUsername,
            @RequestBody UpdatePeerReviewAssignmentRequestDTO request) {
        BaseResponseDTO<List<PeerReviewAssignmentResponseDTO>> response = new BaseResponseDTO<>();
        try {
            List<PeerReviewAssignmentResponseDTO> list = peerReviewAssignmentService.updatePeerReviewAssignments(revieweeUsername, request);
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Peer review assignments untuk reviewee " + revieweeUsername + " berhasil diperbarui");
            response.setTimestamp(new Date());
            response.setData(list);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage(e.getMessage());
            response.setTimestamp(new Date());
            response.setData(null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
}
