package com.pawportal.backend.services.implementations;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/*
    Developed by Group 6:
        Kenji Mark Alan Arceo
        Carl Norbi Felonia
        Ryonan Owen Ferrer
        Dino Alfred Timbol
        Mike Emil Vocal
 */

@Service
@RequiredArgsConstructor
public class ForgotService {

    private final JavaMailSender mailSender;

    public void sendResetPasswordEmail(String toEmail, String token) {
        try {
            String resetLink = "http://localhost:4200/forgot-password?token=" + token;

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("PawPortal - Forgot Password Reset");
            helper.setFrom("your.email@gmail.com");
            helper.setText(generateEmailBody(resetLink), true);

            mailSender.send(message);
            System.out.println("HTML email sent to " + toEmail);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String generateEmailBody(String resetLink) {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <style>
                        body { 
                            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; 
                            margin: 0;
                            padding: 0;
                            background-color: #f5f5f5;
                        }
                        .container { 
                            max-width: 600px; 
                            margin: 0 auto; 
                            padding: 20px; 
                        }
                        .header { 
                            background: linear-gradient(135deg, #1E88E5 0%, #1976D2 100%);
                            color: white; 
                            padding: 30px 20px; 
                            text-align: center; 
                            border-radius: 8px 8px 0 0; 
                        }
                        .header h1 {
                            margin: 0;
                            font-size: 28px;
                        }
                        .content { 
                            background-color: white; 
                            padding: 40px 30px; 
                            border-radius: 0 0 8px 8px;
                            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
                        }
                        .button { 
                            display: inline-block;
                            padding: 15px 40px; 
                            background: linear-gradient(135deg, #FFB74D 0%, #FFA726 100%);
                            color: white !important; 
                            text-decoration: none; 
                            border-radius: 8px; 
                            font-weight: bold;
                            font-size: 16px;
                            margin: 20px 0;
                            text-align: center;
                        }
                        .button:hover {
                            background: linear-gradient(135deg, #FFA726 0%, #FF9800 100%);
                        }
                        .footer { 
                            margin-top: 30px; 
                            padding-top: 20px; 
                            border-top: 1px solid #dee2e6; 
                            color: #6c757d; 
                            font-size: 12px; 
                            text-align: center; 
                        }
                        .warning { 
                            background-color: #fff3cd; 
                            border-left: 4px solid #ffc107; 
                            padding: 15px; 
                            margin: 20px 0;
                            border-radius: 4px;
                        }
                        .warning strong {
                            color: #856404;
                        }
                        .logo {
                            font-size: 36px;
                            margin-bottom: 10px;
                        }
                        p {
                            line-height: 1.6;
                            color: #333;
                        }
                    </style>
                </head>
                <body>
                    <div class='container'>
                        <div class='header'>
                            <div class='logo'>🐾 PAWPORTAL</div>
                            <p style='margin: 0; color: #e3f2fd;'>Dog Adoption Management System</p>
                        </div>
                        <div class='content'>
                            <h2 style='color: #1A1A1A; margin-top: 0;'>Forgot Your Password?</h2>
                            <p>Hello,</p>
                            <p>We received a request to reset your password. Don't worry, it happens to the best of us! Click the button below to create a new password:</p>
                            
                            <div style='text-align: center; margin: 30px 0;'>
                                <a href='""" + resetLink + """
                ' class='button'>Create New Password</a>
                            </div>
                            
                            <p>If the button doesn't work, copy and paste this link into your browser:</p>
                            <p style='background-color: #f8f9fa; padding: 10px; border-radius: 4px; word-break: break-all; font-size: 14px;'>
                                """ + resetLink + """
                            </p>
                            
                            <div class='warning'>
                                <strong>⚠️ Security Notice:</strong>
                                <ul style='margin: 10px 0; padding-left: 20px;'>
                                    <li>This link will expire in <strong>1 hour</strong></li>
                                    <li>For your security, never share this link</li>
                                    <li>If you didn't request this, your account is still secure</li>
                                </ul>
                            </div>
                            
                            <p>If you didn't request a password reset, you can safely ignore this email. Your password will remain unchanged, and no further action is needed.</p>
                            
                            <p style='margin-top: 30px;'>
                                <strong>Questions or concerns?</strong><br>
                                Our team is here to help at <a href='mailto:pawportal.business@outlook.com' style='color: #1E88E5;'>pawportal.business@outlook.com</a><br>
                                Or call us at +63 917 123 4567
                            </p>
                        </div>
                        <div class='footer'>
                            <p>This is an automated message from PawPortal</p>
                            <p>© 2025 PawPortal. All rights reserved.</p>
                            <p>Valenzuela City Veterinary Office Partnership</p>
                        </div>
                    </div>
                </body>
                </html>
                """;
    }
}