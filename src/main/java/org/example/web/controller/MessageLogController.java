package org.example.web.controller;

import lombok.RequiredArgsConstructor;
import org.example.web.data.entity.MessageLog;
import org.example.web.service.MessageLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
