package com.gustavo.therapyclinicsystem.repository;

import com.gustavo.therapyclinicsystem.model.RecurringSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RecurringScheduleRepository extends JpaRepository<RecurringSchedule, UUID> {

    List<RecurringSchedule> findByWorkspaceId(UUID workspaceId);

    List<RecurringSchedule> findByTherapistId(UUID therapistId);

    List<RecurringSchedule> findByPatientId(UUID patientId);

    List<RecurringSchedule> findByWorkspaceIdAndTherapistId(UUID workspaceId, UUID therapistId);

}
