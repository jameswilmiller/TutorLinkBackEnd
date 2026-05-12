package com.tl.tutor_link.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

/**
 * Gmail SMTP configuration. Credentials are pulled from environment
 * variables via application.properties — never commit these directly.
 */
@Configuration
public class EmailConfiguration {

    @Value("${spring.mail.username}")
    private String emailUsername;
    @Value("${spring.mail.password}")
    private String password;

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(AppConstants.SMTP_HOST);
        mailSender.setPort(AppConstants.SMTP_PORT);
        mailSender.setUsername(emailUsername);
        mailSender.setPassword(password);
        mailSender.setJavaMailProperties(mailProperties());
        return mailSender;
    }

    private Properties mailProperties() {
        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.trust", AppConstants.SMTP_HOST);
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        return props;
    }

}
