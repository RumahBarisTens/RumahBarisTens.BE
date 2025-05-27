package propensi.tens.bms.features.trainee_management.controllers;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import propensi.tens.bms.features.trainee_management.dto.request.CreateAssessmentRequest;
import propensi.tens.bms.features.trainee_management.dto.request.UpdateAssessmentRequest;
import propensi.tens.bms.features.trainee_management.dto.response.AssessmentResponse;
import propensi.tens.bms.features.trainee_management.dto.response.BaseResponseDTO;
import propensi.tens.bms.features.trainee_management.services.AssessmentService;

@RestController
@RequestMapping("/api/assessments")
public class AssessmentController {
    @Autowired
    private AssessmentService service;

    @PostMapping
    public ResponseEntity<AssessmentResponse> create(@RequestBody CreateAssessmentRequest req) {
        return ResponseEntity.ok(service.createAssessment(req));
    }

    @GetMapping
    public ResponseEntity<List<AssessmentResponse>> list() {
        return ResponseEntity.ok(service.getAllAssessments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AssessmentResponse> get(@PathVariable("id") Long id) {
        return ResponseEntity.ok(service.getAssessmentById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AssessmentResponse> update(@PathVariable("id") Long id,
                                                     @RequestBody UpdateAssessmentRequest req) {
        return ResponseEntity.ok(service.updateAssessment(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        service.deleteAssessment(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/barista")
    public ResponseEntity<?> getAllBaristas() {
        BaseResponseDTO<List<String>> response = new BaseResponseDTO<>();
        try {
            List<String> reviewees = service.getAllBaristaUsernames();
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

    @GetMapping("/headbar")
    public ResponseEntity<?> getAllHeadBar() {
        BaseResponseDTO<List<String>> response = new BaseResponseDTO<>();
        try {
            List<String> reviewees = service.getAllHeadBarUsernames();
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

    @GetMapping("/traineebarista")
    public ResponseEntity<?> getAllTraineeBarista() {
        BaseResponseDTO<List<String>> response = new BaseResponseDTO<>();
        try {
            List<String> reviewees = service.getAllTraineeBaristaUsernames();
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

    @GetMapping("/access/{username}")
    public ResponseEntity<List<AssessmentResponse>> getByUserId(@PathVariable("username") String username) {
        List<AssessmentResponse> list = service.getAssessmentsByUserId(username);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}/dashboard")
    public ResponseEntity<BaseResponseDTO<Map<String, Object>>> getAssessmentDashboard(
        @PathVariable("id") Long assessmentId,
        @RequestParam(required = false) String outlet  // Filter opsional
    ) {
        BaseResponseDTO<Map<String, Object>> response = new BaseResponseDTO<>();
        try {
            Map<String, Object> data = service.getAssessmentDashboardData(assessmentId);
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Dashboard data berhasil diambil");
            response.setTimestamp(new Date());
            response.setData(data);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage(e.getMessage());
            response.setTimestamp(new Date());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
