package ua.vasylenko.library.v13.Spring.Boot.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.UnsupportedEncodingException;
import java.util.Locale;

@Service
@PropertySource("classpath:application.properties")
public class EmailService {
    private final JavaMailSender mailSender;
    private final TemplateEngine htmlTemplateEngine;
    private final Environment environment;
    @Autowired
    public EmailService(JavaMailSender mailSender, @Qualifier("emailTemplateEngine") TemplateEngine htmlTemplateEngine, Environment environment) {
        this.mailSender = mailSender;
        this.htmlTemplateEngine = htmlTemplateEngine;
        this.environment = environment;
    }

    public void sendSimpleMail(
            final String recipientName, final String recipientEmail, final Locale locale)
            throws MessagingException, UnsupportedEncodingException {

        String TEMPLATE_NAME = "auth/passwordRecoveryMail";
        String SPRING_LOGO_IMAGE = "templates/images/spring.png";
        String PNG_MIME = "image/png";
        String MAIL_SUBJECT = "Password Recovery";

        String mailFrom = environment.getProperty("spring.mail.properties.mail.smtp.from");
        String mailFromName = environment.getProperty("mail.from.name", "Identity");

        final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
        final MimeMessageHelper email = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        email.setTo(recipientEmail);
        email.setSubject(MAIL_SUBJECT);
        email.setFrom(new InternetAddress(mailFrom, mailFromName));

        final Context ctx = new Context(locale);
        ctx.setVariable("email", recipientEmail);
        ctx.setVariable("name", recipientName);
        ctx.setVariable("springLogo", SPRING_LOGO_IMAGE);
        ctx.setVariable("url", "http://localhost:8080/resetPassword?email=" + recipientEmail);

        final String htmlContent = this.htmlTemplateEngine.process(TEMPLATE_NAME, ctx);

        email.setText(htmlContent, true);

        ClassPathResource clr = new ClassPathResource(SPRING_LOGO_IMAGE);

        email.addInline("springLogo", clr, PNG_MIME);

        mailSender.send(mimeMessage);
    }


}
