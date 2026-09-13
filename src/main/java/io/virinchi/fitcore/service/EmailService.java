package io.virinchi.fitcore.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendWelcomeEmail(String to, String name) {

        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("Welcome to FitCore");

            String html =
                    "<!DOCTYPE html>" +
                            "<html>" +
                            "<head>" +
                            "<meta charset='UTF-8'>" +
                            "</head>" +
                            "<body style='margin:0; padding:0; background-color:#111111; font-family:Arial, sans-serif; color:#ffffff;'>" +

                            "<div style='max-width:600px; margin:40px auto; background-color:#222222; border-radius:12px; overflow:hidden;'>" +

                            "<div style='background-color:#111111; padding:25px; text-align:center;'>" +
                            "<h1 style='margin:0; color:#f6c66e; font-size:30px;'>FitCore</h1>" +
                            "</div>" +

                            "<div style='padding:40px 35px; text-align:center;'>" +
                            "<h2 style='margin-top:0; color:#ffffff;'>Welcome to FitCore</h2>" +

                            "<p style='color:#bbbbbb; font-size:16px; line-height:1.6;'>" +
                            "Hi " + name + ",<br><br>" +
                            "Your FitCore account has been successfully created." +
                            "</p>" +

                            "<p style='color:#bbbbbb; font-size:15px; line-height:1.6;'>" +
                            "We're glad to have you with us." +
                            "</p>" +

                            "<p style='margin-top:35px; color:#888888; font-size:13px;'>" +
                            "FitCore Team" +
                            "</p>" +

                            "</div>" +

                            "</div>" +

                            "</body>" +
                            "</html>";

            helper.setText(html, true);

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send welcome email", e);
        }
    }

    public void sendVerificationEmail(
            String to,
            String name,
            String verificationCode) {

        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("Verify your FitCore account");

            String html =
                    "<!DOCTYPE html>" +
                            "<html>" +
                            "<head>" +
                            "<meta charset='UTF-8'>" +
                            "</head>" +
                            "<body style='margin:0; padding:0; background-color:#111111; font-family:Arial, sans-serif; color:#ffffff;'>" +

                            "<div style='max-width:600px; margin:40px auto; background-color:#222222; border-radius:12px; overflow:hidden;'>" +

                            "<div style='background-color:#111111; padding:25px; text-align:center;'>" +
                            "<h1 style='margin:0; color:#f6c66e; font-size:30px;'>FitCore</h1>" +
                            "</div>" +

                            "<div style='padding:40px 35px; text-align:center;'>" +

                            "<h2 style='margin-top:0; color:#ffffff;'>Verify Your Email</h2>" +

                            "<p style='color:#bbbbbb; font-size:16px; line-height:1.6;'>" +
                            "Hi " + name + ",<br><br>" +
                            "Thank you for signing up for FitCore. " +
                            "Please use the verification code below to activate your account." +
                            "</p>" +

                            "<div style='margin:30px 0; padding:20px; background-color:#111111; border:1px solid #f6c66e; border-radius:8px;'>" +
                            "<div style='color:#999999; font-size:13px; margin-bottom:10px;'>YOUR VERIFICATION CODE</div>" +
                            "<div style='color:#f6c66e; font-size:32px; font-weight:bold; letter-spacing:8px;'>" +
                            verificationCode +
                            "</div>" +
                            "</div>" +

                            "<p style='color:#999999; font-size:14px; line-height:1.5;'>" +
                            "Enter this code on the FitCore verification page to activate your account." +
                            "</p>" +

                            "<p style='margin-top:35px; color:#888888; font-size:13px;'>" +
                            "Best regards,<br>" +
                            "FitCore Team" +
                            "</p>" +

                            "</div>" +

                            "</div>" +

                            "</body>" +
                            "</html>";

            helper.setText(html, true);

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send verification email", e);
        }
    }
}