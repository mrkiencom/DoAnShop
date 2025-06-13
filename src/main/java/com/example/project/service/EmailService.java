package com.example.project.service;

import com.example.project.MailProperties;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final MailProperties mail;

    public void sendEmail(final String toEmail, final String subject, final String body) {
        try {
            final MimeMessage message = mailSender.createMimeMessage();
            final MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            final String senderName = "ZES";
            final String fromEmail = mail.getUsername();


            helper.setFrom(new InternetAddress(fromEmail, senderName));
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(body, true);
            mailSender.send(message);

            logger.info("Email đã được gửi thành công tới: {}", toEmail);
        } catch (final Exception ex) {
            logger.error("Gửi email thất bại tới {}: {}", toEmail, ex.getMessage(), ex);
            throw new RuntimeException("Không gửi được email", ex);
        }
    }
}
