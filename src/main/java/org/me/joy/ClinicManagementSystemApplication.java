package org.me.joy;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("org.me.joy.clinic.mapper")
public class ClinicManagementSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(ClinicManagementSystemApplication.class, args);
    }
}