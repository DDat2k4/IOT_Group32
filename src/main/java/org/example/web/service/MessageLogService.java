package org.example.web.service;

import lombok.RequiredArgsConstructor;
import org.example.web.data.entity.MessageLog;
import org.example.web.repository.MessageLogRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageLogService {

    private final MessageLogRepository repository;

    public MessageLog save(MessageLog log) {
        return repository.save(log);
    }

    public List<MessageLog> getAll() {
        return repository.findAll();
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
