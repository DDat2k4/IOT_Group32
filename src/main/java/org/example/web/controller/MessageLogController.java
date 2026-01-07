package org.example.web.controller;

import lombok.RequiredArgsConstructor;
import org.example.web.data.entity.MessageLog;
import org.example.web.data.pojo.ChartPointDTO;
import org.example.web.data.pojo.LatestValueDTO;
import org.example.web.service.MessageLogService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/message-log")
@RequiredArgsConstructor
public class MessageLogController {

    private final MessageLogService messageLogService;

    @GetMapping
    public ResponseEntity<List<MessageLog>> getAll() {
        return ResponseEntity.ok(messageLogService.getAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        messageLogService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/latest")
    public ResponseEntity<LatestValueDTO> getLatestValue(
            @RequestParam String topic,
            @RequestParam String sensorType
    ) {
        LatestValueDTO dto = messageLogService.getLatestValueByTopicAndSensorType(topic, sensorType);

        if (dto == null) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/chart")
    public ResponseEntity<List<ChartPointDTO>> getChartData(
            @RequestParam String topic,
            @RequestParam String sensorType,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime from,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime to,
            @RequestParam(required = false) Integer limit
    ) {
        return ResponseEntity.ok(
                messageLogService.getChartDataAvgPerMinute(
                        topic, sensorType, from, to, limit
                )
        );
    }

}
