package io.virinchi.fitcore.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @org.springframework.beans.factory.annotation.Value("${spring.mail.username}")
    private String adminEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // Sent to the admin inbox whenever someone submits the contact form.
    public void sendContactNotificationToAdmin(
            String senderName,
            String senderEmail,
            String subject,
            String messageBody) {

        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(adminEmail);
            helper.setReplyTo(senderEmail);
            helper.setSubject("[FitCore Contact] " + subject);

            String html =
                    "<!DOCTYPE html>" +
                            "<html>" +
                            "<head><meta charset='UTF-8'></head>" +
                            "<body style='margin:0; padding:0; background-color:#111111; font-family:Arial, sans-serif; color:#ffffff;'>" +
                            "<div style='max-width:600px; margin:40px auto; background-color:#222222; border-radius:12px; overflow:hidden;'>" +
                            "<div style='background-color:#111111; padding:25px; text-align:center;'>" +
                            "<h1 style='margin:0; color:#f6c66e; font-size:30px;'>FitCore</h1>" +
                            "</div>" +
                            "<div style='padding:35px;'>" +
                            "<h2 style='margin-top:0; color:#ffffff;'>New Contact Message</h2>" +
                            "<p style='color:#bbbbbb; font-size:15px;'><strong>From:</strong> " + senderName + " (" + senderEmail + ")</p>" +
                            "<p style='color:#bbbbbb; font-size:15px;'><strong>Subject:</strong> " + subject + "</p>" +
                            "<div style='margin-top:20px; padding:20px; background-color:#111111; border:1px solid #333; border-radius:8px; color:#dddddd; font-size:14px; line-height:1.6;'>" +
                            messageBody +
                            "</div>" +
                            "<p style='margin-top:30px; color:#888888; font-size:13px;'>Reply to this email to respond directly, or reply from the FitCore admin dashboard.</p>" +
                            "</div>" +
                            "</div>" +
                            "</body>" +
                            "</html>";

            helper.setText(html, true);

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send contact notification email", e);
        }
    }

    // Sent to the original sender once the admin replies from the dashboard.
    public void sendContactReplyToUser(
            String toEmail,
            String recipientName,
            String originalSubject,
            String originalMessage,
            String replyText) {

        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Re: " + originalSubject + " - FitCore");

            String html =
                    "<!DOCTYPE html>" +
                            "<html>" +
                            "<head><meta charset='UTF-8'></head>" +
                            "<body style='margin:0; padding:0; background-color:#111111; font-family:Arial, sans-serif; color:#ffffff;'>" +
                            "<div style='max-width:600px; margin:40px auto; background-color:#222222; border-radius:12px; overflow:hidden;'>" +
                            "<div style='background-color:#111111; padding:25px; text-align:center;'>" +
                            "<h1 style='margin:0; color:#f6c66e; font-size:30px;'>FitCore</h1>" +
                            "</div>" +
                            "<div style='padding:35px;'>" +
                            "<h2 style='margin-top:0; color:#ffffff;'>We've replied to your message</h2>" +
                            "<p style='color:#bbbbbb; font-size:15px;'>Hi " + recipientName + ",</p>" +
                            "<p style='color:#999999; font-size:13px;'>Your original message:</p>" +
                            "<div style='margin-bottom:20px; padding:15px; background-color:#111111; border-left:3px solid #444; color:#999999; font-size:13px; line-height:1.5;'>" +
                            originalMessage +
                            "</div>" +
                            "<p style='color:#999999; font-size:13px;'>Our reply:</p>" +
                            "<div style='padding:20px; background-color:#111111; border:1px solid #f6c66e; border-radius:8px; color:#ffffff; font-size:15px; line-height:1.6;'>" +
                            replyText +
                            "</div>" +
                            "<p style='margin-top:30px; color:#888888; font-size:13px;'>FitCore Team</p>" +
                            "</div>" +
                            "</div>" +
                            "</body>" +
                            "</html>";

            helper.setText(html, true);

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send contact reply email", e);
        }
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