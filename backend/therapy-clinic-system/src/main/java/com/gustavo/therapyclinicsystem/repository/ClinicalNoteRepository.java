package com.gustavo.therapyclinicsystem.repository;

import com.gustavo.therapyclinicsystem.model.ClinicalNote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ClinicalNoteRepository extends JpaRepository<ClinicalNote, UUID> {

    List<ClinicalNote> findByPatientId(UUID patientId);

    List<ClinicalNote> findByTherapistId(UUID therapistId);

    List<ClinicalNote> findByPatientIdAndTherapistId(UUID patientId, UUID therapistId);
}
