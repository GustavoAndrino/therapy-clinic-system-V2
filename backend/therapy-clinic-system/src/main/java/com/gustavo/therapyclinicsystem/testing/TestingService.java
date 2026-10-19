package com.gustavo.therapyclinicsystem.testing;


//TODO delete later, this is to facilitate some testings

import com.gustavo.therapyclinicsystem.exception.ResourceNotFoundException;
import com.gustavo.therapyclinicsystem.model.User;
import com.gustavo.therapyclinicsystem.repository.UserRepository;
import com.gustavo.therapyclinicsystem.repository.WorkspaceMembershipRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@Profile("dev")
public class TestingService {

    private final UserRepository userRepository;
    private final WorkspaceMembershipRepository workspaceMembershipRepository;
    private final PasswordEncoder passwordEncoder;

    public TestingService(
            UserRepository userRepository, WorkspaceMembershipRepository workspaceMembershipRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.workspaceMembershipRepository = workspaceMembershipRepository;
        this.passwordEncoder = passwordEncoder;
    }


    public UUID getUserId (String email){

        System.out.println(email);
        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new ResourceNotFoundException(
                        "User not found"
                )
        );

        return user.getId();
    }

    @Transactional(readOnly = true)
    public TestingUserDetailsResponse getUserDetails(String email) {

        String normalizedEmail = email
                .trim()
                .toLowerCase(Locale.ROOT);

        User user = userRepository
                .findByEmail(normalizedEmail)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        )
                );

        List<TestingUserDetailsResponse.MembershipInfo> memberships =
                workspaceMembershipRepository
                        .findAllByUserId(user.getId())
                        .stream()
                        .map(membership ->
                                new TestingUserDetailsResponse.MembershipInfo(
                                        membership.getId(),
                                        membership.getRole(),
                                        membership.getWorkspace().getId(),
                                        membership.getWorkspace().getName(),
                                        membership.getWorkspace().getPlan()
                                )
                        )
                        .toList();

        return new TestingUserDetailsResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getPasswordHash(),
                user.getActive(),
                memberships
        );
    }
}
