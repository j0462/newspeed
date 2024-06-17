package com.example.newspeed;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaAuditing
@SpringBootApplication
@EnableAspectJAutoProxy
@EnableJpaRepositories(basePackages = "com.example.newspeed.repository")
public class NewspeedApplication {

    public static void main(String[] args) {
        SpringApplication.run(NewspeedApplication.class, args);
    }

}
