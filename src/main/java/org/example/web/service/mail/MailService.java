package org.example.web.service.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.example.web.data.entity.Alert;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    @Async
    public void sendAlertEmail(String toEmail, Alert alert) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(toEmail);
            helper.setSubject("[ALERT] Device " + alert.getDevice().getDeviceCode() + " Warning");

            String content = buildEmailContent(alert);
            helper.setText(content, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private String buildEmailContent(Alert alert) {
        boolean warning = alert.getThreshold() != null && alert.getValue() > alert.getThreshold();

        // Chuyển LocalDateTime sang múi giờ VN
        String formattedTime = alert.getCreatedAt()
                .atZone(ZoneId.systemDefault())
                .withZoneSameInstant(ZoneId.of("Asia/Ho_Chi_Minh"))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        StringBuilder sb = new StringBuilder();
        sb.append("<h3>Device Alert Notification</h3>");
        sb.append("<p><strong>Device:</strong> ").append(alert.getDevice().getDeviceCode()).append("</p>");
        sb.append("<p><strong>Sensor:</strong> ").append(
                alert.getSensor() != null ? alert.getSensor().getSensorType() : "N/A").append("</p>");
        sb.append("<p><strong>Value:</strong> ").append(alert.getValue()).append("</p>");
        sb.append("<p><strong>Threshold:</strong> ").append(alert.getThreshold()).append("</p>");
        sb.append("<p><strong>Alert Type:</strong> ").append(alert.getAlertType()).append("</p>");
        sb.append("<p><strong>Status:</strong> ").append(warning ? "WARNING" : "NORMAL").append("</p>");
        sb.append("<p><strong>Time:</strong> ").append(formattedTime).append("</p>");
        return sb.toString();
    }
}

