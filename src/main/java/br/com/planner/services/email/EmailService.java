package br.com.planner.services.email;

import br.com.planner.domain.Owner;
import br.com.planner.dto.email.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.MailMessage;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmailToOwner(Email email) {

    }

    public void sendEmailToParticipant(Email email) {
        try {
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setFrom(email.getFrom());
            String[] recipients = email.getTo().toArray(new String[0]);
            simpleMailMessage.setTo(recipients);
            simpleMailMessage.setSubject(email.getSubject());
            simpleMailMessage.setText(email.getBody());
            mailSender.send(simpleMailMessage);
            System.out.println("Emails enviados");
        } catch (MailException e) {
            System.out.println(e.getMessage());
        }
    }
}
