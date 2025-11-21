package org.example.web.service;

import org.example.web.data.entity.Alert;
import org.example.web.repository.AlertRepository;
import org.springframework.stereotype.Service;

@Service
public class AlertService {

    private final AlertRepository alertRepository;

    public AlertService(AlertRepository alertRepository) {
        this.alertRepository = alertRepository;
    }

    public Alert saveAlert(Alert alert) {
        return alertRepository.save(alert);
    }
}


