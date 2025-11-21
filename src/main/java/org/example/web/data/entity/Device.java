package org.example.web.data.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "device")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 100)
    private String deviceCode;     // ESP32-001

    private String name;
    private String location;

    @Column(length = 20)
    private String status;         // ACTIVE / INACTIVE / ERROR

    private LocalDateTime createdAt = LocalDateTime.now();

    // 1 device → nhiều sensor
    @OneToMany(mappedBy = "device", cascade = CascadeType.ALL)
    private List<Sensor> sensors;
}

