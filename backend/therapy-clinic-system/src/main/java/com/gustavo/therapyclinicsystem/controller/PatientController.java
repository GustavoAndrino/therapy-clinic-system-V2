package com.gustavo.therapyclinicsystem.controller;

import com.gustavo.therapyclinicsystem.dto.patient.CreatePatientRequest;
import com.gustavo.therapyclinicsystem.dto.patient.PatientDetailsResponse;
import com.gustavo.therapyclinicsystem.dto.patient.PatientSummaryResponse;
import com.gustavo.therapyclinicsystem.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/patients")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PatientDetailsResponse createPatient(@Valid @RequestBody CreatePatientRequest request){
        return patientService.createPatient(request);
    }

    @GetMapping("/{patientId}")
    public PatientDetailsResponse getPatientById(@PathVariable UUID patientId){
        return patientService.getPatientById(patientId);
    }

    @GetMapping("/workspace/{workspaceId}")
    public List<PatientSummaryResponse> getPatientsByWorkspace(@PathVariable UUID workspaceId) {
        return patientService.getPatientsByWorkspace(workspaceId);
    }

    @GetMapping("/therapist/{therapistId}")
    public List<PatientSummaryResponse> getPatientsByTherapist(@PathVariable UUID therapistId) {
        return patientService.getPatientsByTherapist(therapistId);
    }


}
