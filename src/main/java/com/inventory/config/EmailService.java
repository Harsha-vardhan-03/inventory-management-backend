package com.inventory.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    
    private final JavaMailSender mailSender;
    
    @Value("${app.email.from:harsharaju203@gmail.com}")
    private String fromEmail;
    
    @Async
    public void sendInvitationEmail(String to, String name, String temporaryPassword) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject("Welcome to Inventory Management System - Your Invitation");
            
            String htmlContent = buildInvitationEmailHtml(name, temporaryPassword);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
            
        } catch (Exception e) {
            log.error("Failed to send staff invitation email to {}", to, e);
        }
    }
    
    private String buildInvitationEmailHtml(String name, String temporaryPassword) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body {
                        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                        line-height: 1.6;
                        color: #333;
                        margin: 0;
                        padding: 0;
                    }
                    .container {
                        max-width: 600px;
                        margin: 0 auto;
                        padding: 20px;
                        background: #f9f9f9;
                        border-radius: 10px;
                    }
                    .header {
                        background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%);
                        color: white;
                        padding: 20px;
                        text-align: center;
                        border-radius: 10px 10px 0 0;
                    }
                    .content {
                        background: white;
                        padding: 30px;
                        border-radius: 0 0 10px 10px;
                        box-shadow: 0 2px 5px rgba(0,0,0,0.1);
                    }
                    .password-box {
                        background: #f0f0f0;
                        padding: 15px;
                        text-align: center;
                        font-size: 24px;
                        font-weight: bold;
                        letter-spacing: 2px;
                        border-radius: 5px;
                        margin: 20px 0;
                        font-family: monospace;
                    }
                    .warning-box {
                        background: #fff3cd;
                        border-left: 4px solid #ffc107;
                        padding: 15px;
                        margin: 20px 0;
                        border-radius: 5px;
                    }
                    .button {
                        display: inline-block;
                        padding: 12px 24px;
                        background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%);
                        color: white;
                        text-decoration: none;
                        border-radius: 5px;
                        margin-top: 20px;
                    }
                    .footer {
                        text-align: center;
                        padding-top: 20px;
                        font-size: 12px;
                        color: #999;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h2>Inventory Management System</h2>
                    </div>
                    <div class="content">
                        <h3>Dear %s,</h3>
                        <p>You have been invited to join the <strong>Inventory Management System</strong> as a staff member.</p>
                        <p>Your account has been created successfully. Please find your temporary login credentials below:</p>
                        
                        <div class="password-box">
                            🔐 <strong>%s</strong>
                        </div>
                        
                        <div class="warning-box">
                            <strong>⚠️ Important Notes:</strong>
                            <ul style="margin: 10px 0 0 20px;">
                                <li>This password is <strong>temporary and expires in 24 hours</strong></li>
                                <li>You <strong>must change your password</strong> upon first login</li>
                                <li>For security reasons, do not share this password with anyone</li>
                            </ul>
                        </div>
                        
                        <center>
                            <a href="http://localhost:4200/login" class="button">🔑 Login to Your Account</a>
                        </center>
                        
                        <p>If you have any questions, please contact your system administrator.</p>
                        <p>Best regards,<br><strong>Inventory Management Team</strong></p>
                    </div>
                    <div class="footer">
                        <p>This is an automated message, please do not reply to this email.</p>
                        <p>© 2026 Inventory Management System. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
            """, name, temporaryPassword);
    }
    
    @Async
    public void sendPasswordResetEmail(String to, String name, String resetToken) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject("Password Reset Request - Inventory Management System");
            
            String resetLink = "http://localhost:4200/reset-password?token=" + resetToken;
            String htmlContent = String.format("""
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <style>
                        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; line-height: 1.6; color: #333; }
                        .container { max-width: 600px; margin: 0 auto; padding: 20px; background: #f9f9f9; border-radius: 10px; }
                        .header { background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); color: white; padding: 20px; text-align: center; border-radius: 10px 10px 0 0; }
                        .content { background: white; padding: 30px; border-radius: 0 0 10px 10px; }
                        .button { display: inline-block; padding: 12px 24px; background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); color: white; text-decoration: none; border-radius: 5px; margin: 20px 0; }
                        .warning { background: #f8d7da; border-left: 4px solid #dc3545; padding: 15px; margin: 20px 0; border-radius: 5px; font-size: 14px; }
                        .footer { text-align: center; padding-top: 20px; font-size: 12px; color: #999; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h2>Password Reset Request</h2>
                        </div>
                        <div class="content">
                            <h3>Dear %s,</h3>
                            <p>We received a request to reset your password for your Inventory Management System account.</p>
                            <center>
                                <a href="%s" class="button">🔐 Reset My Password</a>
                            </center>
                            <div class="warning">
                                <strong>⚠️ Security Notice:</strong><br>
                                • This password reset link will expire in <strong>1 hour</strong><br>
                                • If you didn't request this, please ignore this email<br>
                                • Your password will remain unchanged
                            </div>
                            <p>Best regards,<br><strong>Inventory Management Team</strong></p>
                        </div>
                        <div class="footer">
                            <p>If you're having trouble clicking the button, copy and paste this link:</p>
                            <p style="word-break: break-all; font-size: 11px;">%s</p>
                        </div>
                    </div>
                </body>
                </html>
                """, name, resetLink, resetLink);
            
            helper.setText(htmlContent, true);
            mailSender.send(message);
            
        } catch (MessagingException e) {
            // Silent fail
        }
    }
    
    @Async
    public void sendWelcomeEmail(String to, String name) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject("Welcome to Inventory Management System");
            
            String htmlContent = String.format("""
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <style>
                        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; line-height: 1.6; color: #333; }
                        .container { max-width: 600px; margin: 0 auto; padding: 20px; background: #f9f9f9; border-radius: 10px; }
                        .header { background: linear-gradient(135deg, #28a745 0%%, #20c997 100%%); color: white; padding: 20px; text-align: center; border-radius: 10px 10px 0 0; }
                        .content { background: white; padding: 30px; border-radius: 0 0 10px 10px; }
                        .features { background: #f8f9fa; padding: 15px; border-radius: 5px; margin: 20px 0; }
                        .footer { text-align: center; padding-top: 20px; font-size: 12px; color: #999; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h2>🎉 Welcome to Inventory Management System!</h2>
                        </div>
                        <div class="content">
                            <h3>Welcome %s,</h3>
                            <p>Thank you for joining <strong>Inventory Management System</strong>. We're excited to have you on board!</p>
                            <div class="features">
                                <strong>✨ You now have access to powerful features including:</strong>
                                <ul>
                                    <li>📦 Inventory tracking and management</li>
                                    <li>📋 Purchase order management</li>
                                    <li>💰 Sales and invoicing</li>
                                    <li>📊 Real-time reports and analytics</li>
                                    <li>🏭 Supplier management</li>
                                </ul>
                            </div>
                            <p>To get started, log in to your account and explore the dashboard.</p>
                            <p>If you need any assistance, please don't hesitate to contact our support team.</p>
                            <p>Best regards,<br><strong>Inventory Management Team</strong></p>
                        </div>
                        <div class="footer">
                            <p>© 2026 Inventory Management System. All rights reserved.</p>
                        </div>
                    </div>
                </body>
                </html>
                """, name);
            
            helper.setText(htmlContent, true);
            mailSender.send(message);
            
        } catch (MessagingException e) {
            // Silent fail
        }
    }
}
