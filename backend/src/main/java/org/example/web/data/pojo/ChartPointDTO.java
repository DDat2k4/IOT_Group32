package org.example.web.data.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ChartPointDTO {
    private LocalDateTime time;
    private Double value;
}
