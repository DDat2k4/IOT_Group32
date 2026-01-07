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
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageLogService {

    private final MessageLogRepository repository;
    private final ObjectMapper objectMapper;


    private boolean hasSensorType(String payload, String sensorType) {
        try {
            JsonNode node = objectMapper.readTree(payload);
            return node.has("sensorType")
                    && sensorType.equals(node.get("sensorType").asText());
        } catch (Exception e) {
            return false;
        }
    }

    private Double extractSensorValue(String payload) {
        try {
            JsonNode node = objectMapper.readTree(payload);
            if (!node.has("value")) return null;
            return node.get("value").asDouble();
        } catch (Exception e) {
            return null;
        }
    }

    private LocalDateTime truncateToMinute(LocalDateTime time) {
        return time.withSecond(0).withNano(0);
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

    public MessageLog getLatestByTopicAndSensorType(
            String topic,
            String sensorType
    ) {
        return repository.findByTopicOrderByReceivedAtDesc(topic)
                .stream()
                .filter(log -> hasSensorType(log.getPayload(), sensorType))
                .findFirst()
                .orElse(null);
    }

    public LatestValueDTO getLatestValueByTopicAndSensorType(
            String topic,
            String sensorType
    ) {
        MessageLog log = getLatestByTopicAndSensorType(topic, sensorType);
        if (log == null) return null;

        Double value = extractSensorValue(log.getPayload());
        if (value == null) return null;

        return new LatestValueDTO(value, log.getReceivedAt());
    }

    public List<ChartPointDTO> getChartData(
            String topic,
            String sensorType,
            LocalDateTime from,
            LocalDateTime to,
            Integer limit
    ) {
        List<MessageLog> logs = (from != null && to != null)
                ? repository.findByTopicAndReceivedAtBetweenOrderByReceivedAtAsc(topic, from, to)
                : repository.findByTopicOrderByReceivedAtDesc(topic);

        return logs.stream()
                .filter(log -> hasSensorType(log.getPayload(), sensorType))
                .map(this::toChartPoint)
                .filter(Objects::nonNull)
                .limit(limit != null ? limit : Long.MAX_VALUE)
                .toList();
    }

    private ChartPointDTO toChartPoint(MessageLog log) {
        Double value = extractSensorValue(log.getPayload());
        if (value == null) return null;
        return new ChartPointDTO(log.getReceivedAt(), value);
    }


    public List<ChartPointDTO> getChartDataAvgPerMinute(
            String topic,
            String sensorType,
            LocalDateTime from,
            LocalDateTime to,
            Integer limit
    ) {
        boolean hasFromTo = (from != null && to != null);

        if (!hasFromTo && limit != null) {
            return repository.findByTopicOrderByReceivedAtDesc(topic)
                    .stream()
                    .filter(log -> hasSensorType(log.getPayload(), sensorType))
                    .map(this::toChartPoint)
                    .filter(Objects::nonNull)
                    .limit(limit)
                    .sorted((a, b) -> a.getTime().compareTo(b.getTime()))
                    .toList();
        }

        if (!hasFromTo) {
            to = LocalDateTime.now();
            from = to.minusHours(1);
        }

        List<MessageLog> logs =
                repository.findByTopicAndReceivedAtBetweenOrderByReceivedAtAsc(
                        topic, from, to
                );

        return logs.stream()
                .filter(log -> hasSensorType(log.getPayload(), sensorType))
                .map(log -> {
                    Double value = extractSensorValue(log.getPayload());
                    if (value == null) return null;

                    return Map.entry(
                            truncateTo2Minutes(log.getReceivedAt()),
                            value
                    );
                })
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        Collectors.averagingDouble(Map.Entry::getValue)
                ))
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> new ChartPointDTO(e.getKey(), e.getValue()))
                .toList();
    }

    private LocalDateTime truncateTo2Minutes(LocalDateTime time) {
        int minute = time.getMinute();
        int truncatedMinute = (minute / 2) * 2;
        return time.withMinute(truncatedMinute).withSecond(0).withNano(0);
    }

}
