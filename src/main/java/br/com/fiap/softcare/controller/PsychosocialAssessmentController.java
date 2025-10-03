package br.com.fiap.softcare.controller;

import br.com.fiap.softcare.dto.CreatePsychosocialAssessmentRequest;
import br.com.fiap.softcare.enums.RiskLevel;
import br.com.fiap.softcare.model.PsychosocialAssessment;
import br.com.fiap.softcare.service.PsychosocialAssessmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * REST Controller for Psychosocial Assessment operations
 */
@Slf4j
@RestController
@RequestMapping("/psychosocial-assessments")
@RequiredArgsConstructor
public class PsychosocialAssessmentController {
    
    private final PsychosocialAssessmentService assessmentService;
    
    /**
     * Create a new psychosocial assessment
     */
    @PostMapping
    public ResponseEntity<PsychosocialAssessment> createAssessment(@Valid @RequestBody CreatePsychosocialAssessmentRequest request) {
        log.info("Creating psychosocial assessment for user: {}", request.getUserId());
        
        PsychosocialAssessment assessment = PsychosocialAssessment.builder()
                .userId(request.getUserId())
                .workStressLevel(request.getWorkStressLevel())
                .workLifeBalance(request.getWorkLifeBalance())
                .jobSatisfaction(request.getJobSatisfaction())
                .relationshipWithColleagues(request.getRelationshipWithColleagues())
                .personalWellbeing(request.getPersonalWellbeing())
                .build();
        
        PsychosocialAssessment createdAssessment = assessmentService.createAssessment(assessment);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAssessment);
    }
    
    /**
     * Get assessment by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<PsychosocialAssessment> getAssessmentById(@PathVariable String id) {
        log.info("Getting assessment by ID: {}", id);
        
        Optional<PsychosocialAssessment> assessment = assessmentService.findById(id);
        
        return assessment.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Get assessments by user ID
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PsychosocialAssessment>> getAssessmentsByUserId(@PathVariable String userId) {
        log.info("Getting assessments for user: {}", userId);
        
        List<PsychosocialAssessment> assessments = assessmentService.findByUserId(userId);
        
        return ResponseEntity.ok(assessments);
    }
    
    /**
     * Get latest assessment for user
     */
    @GetMapping("/user/{userId}/latest")
    public ResponseEntity<PsychosocialAssessment> getLatestAssessment(@PathVariable String userId) {
        log.info("Getting latest assessment for user: {}", userId);
        
        Optional<PsychosocialAssessment> assessment = assessmentService.findLatestByUserId(userId);
        
        return assessment.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Update assessment
     */
    @PutMapping("/{id}")
    public ResponseEntity<PsychosocialAssessment> updateAssessment(@PathVariable String id,
                                                                   @Valid @RequestBody CreatePsychosocialAssessmentRequest request) {
        log.info("Updating assessment: {}", id);
        
        try {
            PsychosocialAssessment updatedAssessment = PsychosocialAssessment.builder()
                    .workStressLevel(request.getWorkStressLevel())
                    .workLifeBalance(request.getWorkLifeBalance())
                    .jobSatisfaction(request.getJobSatisfaction())
                    .relationshipWithColleagues(request.getRelationshipWithColleagues())
                    .personalWellbeing(request.getPersonalWellbeing())
                    .build();
            
            PsychosocialAssessment savedAssessment = assessmentService.updateAssessment(id, updatedAssessment);
            return ResponseEntity.ok(savedAssessment);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Delete assessment
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAssessment(@PathVariable String id) {
        log.info("Deleting assessment: {}", id);
        
        try {
            assessmentService.deleteAssessment(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Get assessments by risk level
     */
    @GetMapping("/risk-level/{riskLevel}")
    public ResponseEntity<List<PsychosocialAssessment>> getAssessmentsByRiskLevel(@PathVariable RiskLevel riskLevel) {
        log.info("Getting assessments by risk level: {}", riskLevel);
        
        List<PsychosocialAssessment> assessments = assessmentService.findByRiskLevel(riskLevel);
        
        return ResponseEntity.ok(assessments);
    }
    
    /**
     * Get high risk assessments
     */
    @GetMapping("/high-risk")
    public ResponseEntity<List<PsychosocialAssessment>> getHighRiskAssessments() {
        log.info("Getting high risk assessments");
        
        List<PsychosocialAssessment> assessments = assessmentService.findHighRiskAssessments();
        
        return ResponseEntity.ok(assessments);
    }
    
    /**
     * Get assessments in date range
     */
    @GetMapping("/date-range")
    public ResponseEntity<List<PsychosocialAssessment>> getAssessmentsInDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        log.info("Getting assessments from {} to {}", startDate, endDate);
        
        List<PsychosocialAssessment> assessments = assessmentService.findAssessmentsInDateRange(startDate, endDate);
        
        return ResponseEntity.ok(assessments);
    }
}