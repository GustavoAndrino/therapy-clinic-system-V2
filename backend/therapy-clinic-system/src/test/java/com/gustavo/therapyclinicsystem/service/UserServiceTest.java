package com.gustavo.therapyclinicsystem.service;

import com.gustavo.therapyclinicsystem.dto.user.CreateUserRequest;
import com.gustavo.therapyclinicsystem.model.User;
import com.gustavo.therapyclinicsystem.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

        @Mock
        private UserRepository userRepository;

        @Mock
        private PasswordEncoder passwordEncoder;

        @InjectMocks
        private UserService userService;

    @Test
    void createUser_shouldEncodePasswordBeforeSaving() {

        CreateUserRequest request = new CreateUserRequest(
                "Gustavo",
                "gustavo@email.com",
                "MyPassword123"
        );

        when(passwordEncoder.encode("MyPassword123"))
                .thenReturn("encoded-password");

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        userService.createUser(request);

        ArgumentCaptor<User> userCaptor =
                ArgumentCaptor.forClass(User.class);

        verify(userRepository)
                .save(userCaptor.capture());

        User userToSave = userCaptor.getValue();

        assertEquals(
                "encoded-password",
                userToSave.getPasswordHash()
        );

        assertNotEquals(
                "MyPassword123",
                userToSave.getPasswordHash()
        );

        verify(passwordEncoder)
                .encode("MyPassword123");
    }
    }

