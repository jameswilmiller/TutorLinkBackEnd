package com.tl.tutor_link.notification.service;

import com.tl.tutor_link.auth.service.EmailService;
import com.tl.tutor_link.common.exception.EmailSendException;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Sends transactional emails for user-facing events (verification, bookings,
 * reviews, enquiries). Wraps {@link EmailService} with logging and converts
 * mail exceptions to the application's exception type so callers don't deal
 * with raw javax.mail errors.
 */
@Service
public class NotificationService {
    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final EmailService emailService;

    public NotificationService(EmailService emailService) {
        this.emailService = emailService;
    }

    /**
     * Sends an HTML email. Logs success and failure, converts mail errors
     * to {@link EmailSendException}.
     *
     * @param to recipient email address
     * @param subject email subject line
     * @param htmlBody HTML email body
     * @param contextDescription short description used in logs (e.g. "booking accepted email")
     */
    public void send(String to, String subject, String htmlBody, String contextDescription) {
        try {
            emailService.sendHtmlEmail(to, subject, htmlBody);
            log.info("Sent {} to {}", contextDescription, to);
        } catch (MessagingException e) {
            log.error("Failed to send {} to {}", contextDescription, to, e);
            throw new EmailSendException("Failed to send email");

        }
    }
}
