package com.prz.edu.pl.ogloszone.exception;

import jakarta.mail.MessagingException;

public class EmailSendException extends RuntimeException {
    public EmailSendException(MessagingException e) {
        super("Email sending failed: " + e.getMessage());
    }
}
