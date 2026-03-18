package com.gustavo.therapyclinicsystem.repository;

import com.gustavo.therapyclinicsystem.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PatientRepository extends JpaRepository <Patient, UUID> {

    //Create dto and List<PatientDto> findAllProjectedBy();
    List<Patient> findByWorkspaceId(UUID workspaceId);

    List<Patient> findByTherapistId(UUID therapistId);

    List<Patient> findByWorkspaceIdAndTherapistId(UUID workspaceId, UUID therapistId);

    long countByWorkspaceId(UUID workspaceId);
}
