package com.gustavo.therapyclinicsystem.repository;

import com.gustavo.therapyclinicsystem.model.User;
import com.gustavo.therapyclinicsystem.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    List<User> findByWorkspaceId(UUID workspaceId);

    List<User> findByWorkspaceIdAndRole(UUID workspaceId, UserRole role);

    long countByWorkspaceId(UUID workspaceId);
}
