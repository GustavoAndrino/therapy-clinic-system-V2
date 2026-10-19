package com.gustavo.therapyclinicsystem.testing;

import com.gustavo.therapyclinicsystem.service.PatientService;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

//TODO delete later, this is to facilitate some testings
@RestController
@RequestMapping("/test")
@Profile("dev")
public class TestingController {

    private final TestingService testingService;

    public TestingController(TestingService testingService) {
        this.testingService = testingService;
    }

    @GetMapping("/userId")
    public UUID userId (@RequestBody String email){
        return testingService.getUserId(email);
    }


    @GetMapping("/user-details")
    public TestingUserDetailsResponse getUserDetails(
            @RequestParam String email
    ) {
        return testingService.getUserDetails(email);
    }
}
