package com.tl.tutor_link.auth.service;

import com.tl.tutor_link.auth.dto.LoginUserDto;
import com.tl.tutor_link.auth.dto.RegisterUserDto;
import com.tl.tutor_link.auth.dto.VerifyUserDto;
import com.tl.tutor_link.common.exception.EmailSendException;
import com.tl.tutor_link.common.exception.ResourceNotFoundException;
import com.tl.tutor_link.common.exception.UnauthorizedException;
import com.tl.tutor_link.common.exception.BadRequestException;
import com.tl.tutor_link.user.model.Role;
import com.tl.tutor_link.user.model.User;
import com.tl.tutor_link.user.repository.UserRepository;
import jakarta.mail.MessagingException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    public AuthenticationService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            EmailService emailService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.emailService = emailService;
    }

    public User signup(RegisterUserDto input) {
        User user = new User(input.getFirstname(), input.getLastname(),input.getUsername(), input.getEmail(), passwordEncoder.encode(input.getPassword()));
        user.setVerificationCode(generateVerificationCode());
        user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(15));
        user.setEnabled(false);
        user.getRoles().add(Role.STUDENT);
        sendVerificationEmail(user);
        return userRepository.save(user);
    }

    public User authenticate(LoginUserDto input) {
        User user = userRepository.findByEmail(input.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));
        if (!user.isEnabled()) {
            throw new UnauthorizedException("Account is not verified. Please verify your email");
        }
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()
                )
        );
        return user;
    }

    public void verifyUser(VerifyUserDto input) {
        User user = userRepository.findByEmail(input.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getVerificationCodeExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Verification code has expired");
        }

        if (!user.getVerificationCode().equals(input.getVerificationCode())) {
            throw new BadRequestException("Invalid verification code");
        }

        user.setEnabled(true);
        user.setVerificationCode(null);
        user.setVerificationCodeExpiresAt(null);
        userRepository.save(user);

    }

    public void resendVerificationCode(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.isEnabled()) {
            throw new BadRequestException("Account is already verified");
        }

        user.setVerificationCode(generateVerificationCode());
        user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(15));
        sendVerificationEmail(user);
        userRepository.save(user);
    }

    public void sendVerificationEmail(User user) {
        String subject = "Account verification";
        String verificationCode = user.getVerificationCode();
        String htmlMessage = """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8" />
                <title>Verify your TutorLink account</title>
                <style>
                    body { font-family: Arial, sans-serif; background-color: #f5f5f5; margin: 0; padding: 0; }
                    .container { max-width: 600px; margin: 0 auto; padding: 24px; }
                    .card { background-color: #ffffff; padding: 24px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.08); }
                    h1 { font-size: 20px; margin-bottom: 16px; color: #222222; }
                    p { font-size: 14px; color: #444444; line-height: 1.5; }
                    .code { display: inline-block; margin: 16px 0; padding: 12px 20px; font-size: 20px; letter-spacing: 4px; font-weight: bold; border-radius: 6px; background-color: #eef3ff; color: #2b3a67; }
                    .footer { margin-top: 16px; font-size: 12px; color: #777777; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="card">
                        <h1>Verify your TutorLink account</h1>
                        <p>Hi %s,</p>
                        <p>Thanks for signing up to <strong>TutorLink</strong>! To finish creating your account, please enter the verification code below in the app.</p>
                        <div class="code">%s</div>
                        <p>This code will expire in 15 minutes. If you didn't create a TutorLink account, you can safely ignore this email.</p>
                        <p class="footer">— The TutorLink Team</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(user.getUsername(), verificationCode);

        try {
            emailService.sendVerificationEmail(user.getEmail(), subject, htmlMessage);
        } catch (MessagingException e) {
            throw new EmailSendException("Failed to send verification email");
        }
    }
    private String generateVerificationCode() {
        Random random = new Random();
        int code = random.nextInt(900000) + 100000;
        return String.valueOf(code);
    }
}
