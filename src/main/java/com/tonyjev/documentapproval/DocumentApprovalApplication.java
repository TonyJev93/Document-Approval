package com.tonyjev.documentapproval;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class DocumentApprovalApplication {

    public static void main(String[] args) {
        SpringApplication.run(DocumentApprovalApplication.class, args);
    }

}
