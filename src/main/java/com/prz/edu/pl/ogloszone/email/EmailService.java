package com.prz.edu.pl.ogloszone.email;

import com.prz.edu.pl.ogloszone.add.Add;
import com.prz.edu.pl.ogloszone.exception.EmailSendException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendConfirmationEmail(Add add) {
        try {
            MimeMessage mimeMessage = formEmail(add);
            mailSender.send(mimeMessage);
            logger.info("Sent confirmation email for add ID: {}, to: {}", add.id(), add.contact().email());
        } catch (MessagingException e) {
            logger.error("Failed to send confirmation email to {}: {}", add.contact().email(), e.getMessage());
            throw new EmailSendException(e);
        }
    }

    private MimeMessage formEmail(Add add) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        helper.setTo(add.contact().email());
        helper.setSubject("Potwierdzenie publikacji ogłoszenia: " + add.title());

        String htmlContent = generateContent(add);
        helper.setText(htmlContent, true);
        return mimeMessage;
    }

    private String generateContent(Add add) {
        return "<h1>Cześć!</h1>" +
                "<p>Twoje ogłoszenie: <strong>" + add.title() + "</strong></p>" +
                "<p>Szczegóły:</p>" +
                "<ul>" +
                "<li>Tytuł: " + add.title() + "</li>" +
                "<li>Kategoria: " + add.category() + "</li>" +
                "</ul>" +
//                "<p>Link do edycji: <a href='http://moja-aplikacja.pl/edit/" + add.id() + "?code=" + add.editCode() + "'>Edytuj ogłoszenie</a></p>" +
                "<p>ID ogłoszenia: " + add.id() + "</p>" +
                "<p>Kod do edycji: " + add.editCode() + "</p>" +
                "<p>Ogłoszenie wygasa za 30 dni.</p>" +
                "<p>Pozdrawiamy<br>Zespół OgłosZone</p>";
    }
}