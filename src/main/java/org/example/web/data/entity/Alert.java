package org.example.web.data.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "alert")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

    @ManyToOne
    @JoinColumn(name = "sensor_id")
    private Sensor sensor;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserAccount user;

    @Column(length = 50)
    private String alertType;

    @Column(length = 20)
    private String alertLevel;

    private Float value;

    private Float threshold;

    private String topic;

    @Column(columnDefinition = "TEXT")
    private String payload;

    private Boolean isWarning = true;

    private LocalDateTime createdAt = LocalDateTime.now();
}


