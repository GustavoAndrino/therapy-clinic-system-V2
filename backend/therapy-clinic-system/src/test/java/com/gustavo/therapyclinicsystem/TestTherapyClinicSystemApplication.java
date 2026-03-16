package com.gustavo.therapyclinicsystem;

import org.springframework.boot.SpringApplication;

public class TestTherapyClinicSystemApplication {

    public static void main(String[] args) {
        SpringApplication.from(TherapyClinicSystemApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
