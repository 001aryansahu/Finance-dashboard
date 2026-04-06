package com.example.financeprp;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.example.financeprp.Service.UserService;

@SpringBootApplication
public class FinanceprpApplication {

    public static void main(String[] args) {
        SpringApplication.run(FinanceprpApplication.class, args);
    }

    @Bean
    CommandLineRunner seedAdmin(UserService userService) {
        return args -> userService.createDefaultAdminIfMissing();
    }
}
