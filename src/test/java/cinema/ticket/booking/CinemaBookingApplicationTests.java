package cinema.ticket.booking;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import cinema.ticket.booking.service.impl.EmailServiceImpl;

@SpringBootTest
class CinemaBookingApplicationTests {

	@Test
	void contextLoads() {
	}
	    // sendMail sends an email with the correct 'to' address


    @Test
    public void testEmailSending() throws Exception {
        // Create a mock JavaMailSender
        JavaMailSender mockMailSender = Mockito.mock(JavaMailSender.class);
		ConfigurableApplicationContext context = new SpringApplication(CinemaBookingApplication.class).run();
        // Create an instance of your EmailServiceImpl
        EmailServiceImpl emailService = context.getBean(EmailServiceImpl.class);

        // Set the mock mailSender to your emailService
        ReflectionTestUtils.setField(emailService, "mailSender", mockMailSender);

        // Prepare test data
        String to = "truongxuando4@gmail.com";
        String subject = "Test Subject";
        String text = "Test Body";

        // Call the sendSimpleMessage method
        emailService.sendMail(to, subject, text);

        // Verify that send was called on the mock JavaMailSender
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        Mockito.verify(mockMailSender).send(messageCaptor.capture());

        // Assert the contents of the sent message
        SimpleMailMessage sentMessage = messageCaptor.getValue();
        assertEquals(to, sentMessage.getTo()[0]);
        assertEquals(subject, sentMessage.getSubject());
        assertEquals(text, sentMessage.getText());
    }
}
