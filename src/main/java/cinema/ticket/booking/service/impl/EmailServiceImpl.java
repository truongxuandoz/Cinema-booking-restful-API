package cinema.ticket.booking.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import cinema.ticket.booking.service.EmailService;
import cinema.ticket.booking.utils.MailSender;

@Service
public class EmailServiceImpl implements EmailService {
	
	@Value("${spring.mail.username}")
	private String default_sender;

	private final MailSender mailSender;

	public EmailServiceImpl(MailSender mailSender) {
		this.mailSender = mailSender;
	}

	@Override
	public void sendMail(String toMail, String subject, String body) {
		mailSender.sendMail(toMail, subject, body);
	}

}
