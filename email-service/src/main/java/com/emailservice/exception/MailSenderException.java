package com.emailservice.exception;

import jakarta.mail.MessagingException;

public class MailSenderException extends MessagingException {
    public MailSenderException(String s, Exception e) {
        super(s, e);
    }
}
