package com.fiap.techchallenge.fourlanches;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class FourLanchesAuthApp {

    public static void main(String[] args) {
        SpringApplication.run(FourLanchesAuthApp.class, args);
    }

}
