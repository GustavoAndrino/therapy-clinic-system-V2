package com.gustavo.therapyclinicsystem.security;

import com.gustavo.therapyclinicsystem.model.User;
import com.gustavo.therapyclinicsystem.repository.UserRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

//@Disabled("Enable after integration-test datasource is configured")
@SpringBootTest
@Transactional
class AuthenticationIntegrationTest {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void shouldAuthenticateUserWithCorrectCredentials() {

        String email =
                "auth-test-" + UUID.randomUUID() + "@email.com";

        String rawPassword = "Password123!";

        User user = new User();
        user.setFullName("Authentication Test");
        user.setEmail(email);
        user.setPasswordHash(
                passwordEncoder.encode(rawPassword)
        );
        user.setActive(true);

        User savedUser =
                userRepository.saveAndFlush(user);

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                email,
                                rawPassword
                        )
                );

        assertNotNull(authentication);
        assertTrue(authentication.isAuthenticated());

        assertInstanceOf(
                CustomUserDetails.class,
                authentication.getPrincipal()
        );

        CustomUserDetails principal =
                (CustomUserDetails)
                        authentication.getPrincipal();

        assertEquals(
                savedUser.getId(),
                principal.getId()
        );

        assertEquals(
                email,
                principal.getUsername()
        );

        assertTrue(principal.isEnabled());
    }

    @Test
    void shouldRejectUserWithIncorrectPassword() {

        String email =
                "auth-test-" + UUID.randomUUID() + "@email.com";

        String correctPassword = "CorrectPassword123!";

        User user = new User();
        user.setFullName("Authentication Test");
        user.setEmail(email);
        user.setPasswordHash(
                passwordEncoder.encode(correctPassword)
        );
        user.setActive(true);

        userRepository.saveAndFlush(user);

        assertThrows(
                BadCredentialsException.class,
                () ->
                        authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(
                                        email,
                                        "WrongPassword123!"
                                )
                        )
        );
    }

    @Test
    void shouldRejectCredentialsWhenUserDoesNotExist() {

        String nonexistentEmail =
                "missing-" + UUID.randomUUID() + "@email.com";

        assertThrows(
                BadCredentialsException.class,
                () ->
                        authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(
                                        nonexistentEmail,
                                        "Password123!"
                                )
                        )
        );
    }
}