package com.example.project.service;

import com.example.project.MailProperties;
import com.example.project.exception.NotFoundException;
import com.example.project.model.PasswordResetToken;
import com.example.project.model.Users;
import com.example.project.repo.PasswordResetTokenRepo;
import com.example.project.repo.UserRepo;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AuthenticationService {

    private final UserRepo userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final PasswordResetTokenRepo passwordResetTokenRepo;
    private final MailProperties mailProperties;

    public boolean registerNewUserAccount(final Users user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            return false; // Username đã tồn tại
        }
        if (userRepository.existsByGmail(user.getGmail())) {
            return false;
        }

        final Users newUser = new Users();

        newUser.setUsername(user.getUsername());
        newUser.setFirstname(user.getFirstname());
        newUser.setLastname(user.getLastname());
        newUser.setGmail(user.getGmail());
        newUser.setPassword(passwordEncoder.encode(user.getPassword()));
        newUser.setPicture("/images/default_user.jpg");
        newUser.setRole("user");
        newUser.setCreatedAt(LocalDateTime.now());
        newUser.setActive(true);

        userRepository.save(newUser);
        return true;
    }

    public void saveGmailAccount(final String gmail) {
        final boolean check = userRepository.existsByGmail(gmail);
        if (!check) {
            final Users newUser = new Users();
            newUser.setGmail(gmail);
            newUser.setPicture("/images/default_user.jpg");
            newUser.setRole("user");
            userRepository.save(newUser);
        }
    }

    public Users getInforUser(final String username) {
        return userRepository.findByUsername(username);
    }

    public Users getInforUserByGmail(final String gmail) {
        return userRepository.findByGmail(gmail);
    }

    public boolean changeUserPassword(final Users user, final String newPassword) {
        if (user != null) {
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            return true;
        }
        return false;
    }

    public String genToken(final String email) {
        final String token = UUID.randomUUID().toString();

        final LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(30);

        final PasswordResetToken resetToken = new PasswordResetToken(token, email, expiryDate);
        passwordResetTokenRepo.save(resetToken);

        return token;
    }

    public void sendMailToResetPassword(final String gmail) {
        final var token = genToken(gmail);
        final String link = mailProperties.getDomain() + "/reset-password?token=" + token;
        final String htmlContent = """
                <html lang="en">
                 <head>
                  <meta charset="utf-8"/>
                  <meta content="width=device-width, initial-scale=1" name="viewport"/>
                  <title>Reset Password</title>
                  <style>
                   body {
                      font-family: "Inter", sans-serif;
                      background-color: #2B6CFD;
                      display: flex;
                      justify-content: center;
                      align-items: center;
                      padding: 24px;
                      min-height: 100vh;
                    }
                    .container {
                      background-color: white;
                      border-radius: 12px;
                      max-width: 400px;
                      width: 100%;
                      overflow: hidden;
                    }
                    .header {
                      padding: 40px 0 24px;
                      text-align: center;
                      border-bottom: 1px solid #e5e7eb;
                    }
                    .header img {
                      height: 32px;
                      margin-bottom: 12px;
                    }
                    .header h1 {
                      font-size: 20px;
                      font-weight: 600;
                      color: #0B1437;
                      letter-spacing: 0.05em;
                    }
                    .header h2 {
                      margin-top: 8px;
                      font-size: 18px;
                      font-weight: 600;
                      color: #0B1437;
                    }
                    .content {
                      padding: 32px 40px;
                      color: #0B1437;
                      font-size: 16px;
                      line-height: 1.6;
                    }
                    .content .greeting {
                      font-weight: 600;
                      margin-bottom: 16px;
                    }
                    .content p {
                      margin-bottom: 24px;
                    }
                    .btn {
                      display: inline-block;
                      width: 100%;
                      background-color: #2B6CFD;
                      color: white;
                      font-weight: 600;
                      font-size: 16px;
                      text-align: center;
                      text-decoration: none;
                      border-radius: 8px;
                      padding: 12px 0;
                      transition: background-color 0.3s ease;
                    }
                    .btn:hover {
                      background-color: #2555d9;
                    }
                  </style>
                 </head>
                 <body>
                  <div class="container">
                   <header class="header">
                    <img src="https://storage.googleapis.com/a1aa/image/39a26a83-f462-4f47-5af5-94d642eca472.jpg" alt="Bluassist Logo"/>
                    <h1>ZEN</h1>
                    <h2>Reset your password</h2>
                   </header>
                   <main class="content">
                    <p class="greeting">Hey,</p>
                    <p>Need to reset your password? No problem! Just click the button below and you’ll be on your way. If you did not make this request, please ignore this email.</p>
                    <a href={{link}} class="btn">Reset your password</a>
                   </main>
                  </div>
                 </body>
                </html>
                """;

        emailService.sendEmail(gmail, "Reset password", htmlContent.replace("{{link}}", link));
    }

    public boolean changePassWord(final String newPassword, final String token) {
        final var passResetToken = validateToken(token);
        if (passResetToken.isEmpty()) return false;

        final var email = passResetToken.map(PasswordResetToken::getEmail).orElse("");
        if (email.isBlank()) return false;

        final var user = userRepository.findByGmail(email);
        if (user == null) return false;

        passResetToken.get().setUsed(true);
        passwordResetTokenRepo.save(passResetToken.get());

        return changeUserPassword(user, newPassword);
    }

    public Optional<PasswordResetToken> validateToken(final String token) {
        return passwordResetTokenRepo.findByToken(token)
                .filter(t -> !t.isUsed())
                .filter(t -> t.getExpiryDate().isAfter(LocalDateTime.now()));
    }

    public Users findById(final int id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("Cannot find user has id: " + id));
    }
}
