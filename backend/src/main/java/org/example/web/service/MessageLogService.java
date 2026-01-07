package org.example.web.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.web.data.entity.MessageLog;
import org.example.web.data.pojo.ChartPointDTO;
import org.example.web.data.pojo.LatestValueDTO;
import org.example.web.repository.MessageLogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageLogService {

    private final MessageLogRepository repository;

    private final ObjectMapper objectMapper;

    public MessageLog getLatestByTopicAndSensorType(
            String topic,
            String sensorType
    ) {
        List<MessageLog> logs =
                repository.findByTopicOrderByReceivedAtDesc(topic);

        return logs.stream()
                .filter(log -> hasSensorType(log.getPayload(), sensorType))
                .findFirst()
                .orElse(null);
    }

    private boolean hasSensorType(String payload, String sensorType) {
        try {
            JsonNode node = objectMapper.readTree(payload);
            return sensorType.equals(node.get("sensorType").asText());
        } catch (Exception e) {
            return false;
        }
    }

    public MessageLog save(MessageLog log) {
        return repository.save(log);
    }

    public List<MessageLog> getAll() {
        return repository.findAll();
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public List<ChartPointDTO> getChartData(
            String topic,
            String sensorType,
            LocalDateTime from,
            LocalDateTime to,
            Integer limit
    ) {
        List<MessageLog> logs;

        if (from != null && to != null) {
            logs = repository
                    .findByTopicAndReceivedAtBetweenOrderByReceivedAtAsc(
                            topic, from, to
                    );
        } else {
            logs = repository.findByTopicOrderByReceivedAtDesc(topic);
        }

        return logs.stream()
                .filter(log -> hasSensorType(log.getPayload(), sensorType))
                .map(this::toChartPoint)
                .filter(dto -> dto != null)
                .limit(limit != null ? limit : Long.MAX_VALUE)
                .toList();
    }

    private ChartPointDTO toChartPoint(MessageLog log) {
        try {
            JsonNode node = objectMapper.readTree(log.getPayload());

            if (!node.has("value")) return null;

            return new ChartPointDTO(
                    log.getReceivedAt(),
                    node.get("value").asDouble()
            );
        } catch (Exception e) {
            return null;
        }
    }

    public LatestValueDTO getLatestValueByTopicAndSensorType(String topic, String sensorType) {
        MessageLog log = getLatestByTopicAndSensorType(topic, sensorType);

        if (log == null) return null;

        try {
            JsonNode node = objectMapper.readTree(log.getPayload());
            if (!node.has("value")) return null;

            return new LatestValueDTO(node.get("value").asDouble(), log.getReceivedAt());
        } catch (Exception e) {
            return null;
        }
    }
}
