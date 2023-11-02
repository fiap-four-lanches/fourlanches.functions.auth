package com.fiap.techchallenge.fourlanches.functions;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.function.Function;

@SpringBootApplication
@EnableJpaRepositories
public class FourLanchesAuthApp {
    @Bean
    public Function<String, String> echo() {
        return payload -> payload;
    }
    public static void main(String[] args) {
        SpringApplication.run(FourLanchesAuthApp.class, args);
    }

}
