package com.aidannolan.quizbuilder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class QuizBuilderApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuizBuilderApplication.class, args);
    }

}
