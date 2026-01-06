package org.example.web.data.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "alert_email_log",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "sensor_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertEmailLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "sensor_id", nullable = false)
    private Long sensorId;

    @Column(name = "last_sent", nullable = false)
    private LocalDateTime lastSent;
}