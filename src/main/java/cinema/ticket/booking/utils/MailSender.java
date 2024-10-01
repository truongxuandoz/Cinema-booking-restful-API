package cinema.ticket.booking.utils;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
@Component
public class MailSender {
    private JavaMailSender mailSender;

    public MailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
    public void sendMail(String toMail, String subject, String body) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom("truongxuando4@gmail.com");
        mail.setTo(toMail);
        mail.setSubject(subject);
        mail.setText(body);
        mailSender.send(mail);
    }   
}
