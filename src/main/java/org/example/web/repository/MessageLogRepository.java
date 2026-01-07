package org.example.web.repository;

import org.example.web.data.entity.MessageLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MessageLogRepository extends JpaRepository<MessageLog, Long> {
    List<MessageLog> findByTopicOrderByReceivedAtDesc(String topic);

    List<MessageLog> findByTopicAndReceivedAtBetweenOrderByReceivedAtAsc(
            String topic,
            LocalDateTime from,
            LocalDateTime to
    );
}
