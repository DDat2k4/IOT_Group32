package org.example.web.data.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class LatestValueDTO {
    private double value;
    private LocalDateTime receivedAt;
}
