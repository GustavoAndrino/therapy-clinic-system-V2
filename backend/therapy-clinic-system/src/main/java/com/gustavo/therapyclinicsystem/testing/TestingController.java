package com.gustavo.therapyclinicsystem.testing;

import com.gustavo.therapyclinicsystem.service.PatientService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

//TODO delete later, this is to facilitate some testings
@RestController
@RequestMapping("/test")
public class TestingController {

    private final TestingService testingService;

    public TestingController(TestingService testingService) {
        this.testingService = testingService;
    }

    @GetMapping("/userId")
    public UUID userId (@RequestBody String email){
        return testingService.getUserId(email);
    }

}
