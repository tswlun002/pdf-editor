package com.emailservice.email;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;

class EmailServiceTest {
   private @Mock Environment environment;
    private @Mock JavaMailSender javaMailSender;
    private @InjectMocks EmailService service;
    @BeforeEach
    void setUp() {
        service= new EmailService(javaMailSender,environment);
    }

    @Test
    void sendEmailSuccessful() {

    }
}