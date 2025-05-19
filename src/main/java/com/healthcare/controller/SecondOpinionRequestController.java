package com.healthcare.controller;

import com.healthcare.model.SecondOpinionRequest;
import com.healthcare.service.SecondOpinionRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/second-opinion-requests")
public class SecondOpinionRequestController {

    @Autowired
    private SecondOpinionRequestService service;

    @PostMapping
    public ResponseEntity<?> createRequest(@RequestBody Map<String, Object> requestData) {
        try {
            Long patientId = Long.valueOf(requestData.get("patientId").toString());
            Long requestingDoctorId = Long.valueOf(requestData.get("requestingDoctorId").toString());
            Long consultingDoctorId = Long.valueOf(requestData.get("consultingDoctorId").toString());
            String conditionDescription = (String) requestData.get("conditionDescription");
            String currentTreatment = (String) requestData.get("currentTreatment");
            
            SecondOpinionRequest request = service.createSecondOpinionRequest(
                patientId, requestingDoctorId, consultingDoctorId, conditionDescription, currentTreatment
            );
            
            return new ResponseEntity<>(Map.of(
                "message", "Second opinion request created successfully",
                "requestId", request.getId()
            ), HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRequestById(@PathVariable Long id) {
        Optional<SecondOpinionRequest> request = service.getRequestById(id);
        return request.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<SecondOpinionRequest>> getAllRequests() {
        List<SecondOpinionRequest> requests = service.getAllRequests();
        return ResponseEntity.ok(requests);
    }
    
    @GetMapping("/paginated")
    public ResponseEntity<Page<SecondOpinionRequest>> getAllRequestsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "requestDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        
        Sort.Direction direction = sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        return ResponseEntity.ok(service.getAllRequestsPaginated(pageable));
    }
    
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<SecondOpinionRequest>> getRequestsByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(service.getRequestsByPatient(patientId));
    }
    
    @GetMapping("/patient/{patientId}/paginated")
    public ResponseEntity<Page<SecondOpinionRequest>> getRequestsByPatientPaginated(
            @PathVariable Long patientId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "requestDate"));
        return ResponseEntity.ok(service.getRequestsByPatientPaginated(patientId, pageable));
    }
    
    @GetMapping("/consulting-doctor/{doctorId}")
    public ResponseEntity<List<SecondOpinionRequest>> getRequestsByConsultingDoctor(@PathVariable Long doctorId) {
        return ResponseEntity.ok(service.getRequestsByConsultingDoctor(doctorId));
    }
    
    @GetMapping("/consulting-doctor/{doctorId}/status/{status}")
    public ResponseEntity<List<SecondOpinionRequest>> getRequestsByConsultingDoctorAndStatus(
            @PathVariable Long doctorId,
            @PathVariable String status) {
        return ResponseEntity.ok(service.getRequestsByConsultingDoctorAndStatus(doctorId, status));
    }
    
    @GetMapping("/requesting-doctor/{doctorId}")
    public ResponseEntity<List<SecondOpinionRequest>> getRequestsByRequestingDoctor(@PathVariable Long doctorId) {
        return ResponseEntity.ok(service.getRequestsByRequestingDoctor(doctorId));
    }
    
    @GetMapping("/requesting-doctor/{doctorId}/status/{status}")
    public ResponseEntity<List<SecondOpinionRequest>> getRequestsByRequestingDoctorAndStatus(
            @PathVariable Long doctorId,
            @PathVariable String status) {
        return ResponseEntity.ok(service.getRequestsByRequestingDoctorAndStatus(doctorId, status));
    }
    
    @GetMapping("/date-range")
    public ResponseEntity<List<SecondOpinionRequest>> getRequestsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(service.getRequestsByDateRange(startDate, endDate));
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<SecondOpinionRequest>> searchRequests(@RequestParam String keyword) {
        return ResponseEntity.ok(service.searchRequests(keyword));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateRequest(@PathVariable Long id, @RequestBody SecondOpinionRequest updatedRequest) {
        try {
            SecondOpinionRequest updated = service.updateRequest(id, updatedRequest);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PutMapping("/{id}/provide-opinion")
    public ResponseEntity<?> provideSecondOpinion(
            @PathVariable Long id,
            @RequestBody Map<String, String> opinionData) {
        try {
            String secondOpinion = opinionData.get("secondOpinion");
            SecondOpinionRequest updated = service.provideSecondOpinion(id, secondOpinion);
            return ResponseEntity.ok(Map.of(
                "message", "Second opinion provided successfully",
                "requestId", updated.getId()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PutMapping("/{id}/update-status")
    public ResponseEntity<?> updateRequestStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> statusData) {
        try {
            String status = statusData.get("status");
            SecondOpinionRequest updated = service.updateRequestStatus(id, status);
            return ResponseEntity.ok(Map.of(
                "message", "Request status updated successfully",
                "requestId", updated.getId(),
                "status", updated.getStatus()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRequest(@PathVariable Long id) {
        try {
            service.deleteRequest(id);
            return ResponseEntity.ok(Map.of("message", "Request deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}