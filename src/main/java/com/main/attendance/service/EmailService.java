package com.main.attendance.service;

import com.main.attendance.dto.MailBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;


    public void  sendSimpleMessage(MailBody mailBody) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setText(mailBody.text());
        message.setSubject(mailBody.subject());
        message.setTo(mailBody.to());
        message.setFrom("shawchandan8357@gmail.com");
        javaMailSender.send(message);
    }
}
