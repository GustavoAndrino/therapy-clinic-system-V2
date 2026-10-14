package com.gustavo.therapyclinicsystem.testing;


//TODO delete later, this is to facilitate some testings

import com.gustavo.therapyclinicsystem.exception.ResourceNotFoundException;
import com.gustavo.therapyclinicsystem.model.User;
import com.gustavo.therapyclinicsystem.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TestingService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public TestingService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
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
}
