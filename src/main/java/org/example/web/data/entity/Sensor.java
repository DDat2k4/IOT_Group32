package org.example.web.data.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sensor")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sensor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // nhiều sensor → 1 device
    @ManyToOne
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

    @Column(nullable = false, length = 50)
    private String sensorType;    // MQ2, FLAME, DHT11...

    private String name;
    private String unit;         // ppm, °C, %

    private Float minValue;
    private Float maxValue;

    @Column(length = 20)
    private String status = "ACTIVE";

    private LocalDateTime createdAt = LocalDateTime.now();
}
