package com.banking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender emailSender;

    public void sendVerificationCode(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Password Reset Verification Code");
        message.setText("Your verification code for password reset is: " + code + "\n\n" +
                "This code will expire in 10 minutes.\n" +
                "If you didn't request this, please ignore this email.");
        emailSender.send(message);
    }
} 