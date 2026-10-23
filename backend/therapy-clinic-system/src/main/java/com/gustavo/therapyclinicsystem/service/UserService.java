package com.gustavo.therapyclinicsystem.service;

import com.gustavo.therapyclinicsystem.dto.user.CreateUserRequest;
import com.gustavo.therapyclinicsystem.dto.user.CreateWorkspaceRequest;
import com.gustavo.therapyclinicsystem.dto.user.UserSummaryResponse;
import com.gustavo.therapyclinicsystem.exception.BusinessRuleException;
import com.gustavo.therapyclinicsystem.exception.EmailAlreadyExistsException;
import com.gustavo.therapyclinicsystem.exception.ResourceNotFoundException;
import com.gustavo.therapyclinicsystem.model.User;
import com.gustavo.therapyclinicsystem.model.Workspace;
import com.gustavo.therapyclinicsystem.model.WorkspaceMembership;
import com.gustavo.therapyclinicsystem.model.enums.WorkspacePlan;
import com.gustavo.therapyclinicsystem.model.enums.WorkspaceRole;
import com.gustavo.therapyclinicsystem.repository.UserRepository;
import com.gustavo.therapyclinicsystem.repository.WorkspaceMembershipRepository;
import com.gustavo.therapyclinicsystem.repository.WorkspaceRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.UUID;


@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository, WorkspaceRepository workspaceRepository,
            PasswordEncoder passwordEncoder, WorkspaceMembershipRepository workspaceMembershipRepository
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;    }

    public User createAndSaveUser(CreateUserRequest request) {
        String email = request.email()
                .trim()
                .toLowerCase(Locale.ROOT);

        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(
                    "Email is already registered"
            );
        }

        User user = new User();

        user.setFullName(request.fullName().trim());
        user.setEmail(email);
        user.setPasswordHash(
                passwordEncoder.encode(request.password())
        );
        user.setActive(true);

        return userRepository.save(user);
    }

    //TODO forgot password
    /*public String forgotPassword (String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User with email " + email + "not found"));
    }*/
}
