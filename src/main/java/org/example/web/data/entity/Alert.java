package org.example.web.data.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "alert")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // nhiều alert → 1 device
    @ManyToOne
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

    @ManyToOne
    @JoinColumn(name = "sensor_id")
    private Sensor sensor;

    @Column(length = 50)
    private String alertType;     // FIRE / SMOKE / GAS / TEMP_HIGH...

    private Float value;
    private Float threshold;

    private String topic;

    @Column(columnDefinition = "TEXT")
    private String payload;

    private Boolean isWarning = true;

    private LocalDateTime createdAt = LocalDateTime.now();
}


