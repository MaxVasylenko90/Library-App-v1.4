package ua.vasylenko.library.v13.Spring.Boot.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

@Service
@PropertySource("classpath:mail/emailconfig.properties")
public class EmailService {

//    private static final String EMAIL_SIMPLE_TEMPLATE_NAME = "html/email-simple";
    private static final String SENDER = "mail.server.username";

    private final JavaMailSender mailSender;
    private final TemplateEngine htmlTemplateEngine;
    @Autowired
    public EmailService(JavaMailSender mailSender, @Qualifier("emailTemplateEngine") TemplateEngine htmlTemplateEngine) {
        this.mailSender = mailSender;
        this.htmlTemplateEngine = htmlTemplateEngine;
    }

    public void sendSimpleMail(
            final String recipientName, final String recipientEmail, final Locale locale)
            throws MessagingException {

        // Prepare the evaluation context
        final Context ctx = new Context(locale);
        ctx.setVariable("name", recipientName);
        ctx.setVariable("subscriptionDate", new Date());

        // Prepare message using a Spring helper
        final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
        final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8");
        message.setSubject("Password recovery");
        message.setFrom(SENDER);
        message.setTo(recipientEmail);

        // Create the HTML body using Thymeleaf
        String messageForUser = String.format("Hello %s! You requested a password reset. If you didn't, just ignore this email.\n" +
                        "To reset your password, follow the link:\n"
                        + "http://localhost:8080/resetPassword/user=%s", recipientName, recipientEmail);
        final String htmlContent = this.htmlTemplateEngine.process(messageForUser, ctx);
        message.setText(htmlContent, true /* isHtml */);

        // Send email
        this.mailSender.send(mimeMessage);
    }


}
