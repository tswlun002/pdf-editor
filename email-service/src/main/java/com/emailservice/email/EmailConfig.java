package com.emailservice.email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import java.util.Properties;

@Configuration
public class EmailConfig {
    @Value("${mail.host}")
    private  String HOST_MAIL;
    @Value("${mail.username}")
    private  String EMAIL;
    @Value("${mail.password}")
    private  String PASSWORD;
    @Value("${mail.protocol}")
    private  String PROTOCOL;
    @Value("${mail.port}")
    private  int PORT;
    @Bean
    public JavaMailSender JavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(HOST_MAIL);
        mailSender.setPort(PORT);
        mailSender.setUsername(EMAIL);
        mailSender.setPassword(PASSWORD);
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", PROTOCOL);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        return mailSender;
    }

}
