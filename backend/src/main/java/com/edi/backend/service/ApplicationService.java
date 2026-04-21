package com.edi.backend.service;

import com.edi.backend.domain.AppType;
import com.edi.backend.domain.Application;
import com.edi.backend.repository.ApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;

    public Application getByType(AppType type) {
        return applicationRepository.findByType(type)
                .orElseThrow(() -> new IllegalArgumentException("Application not found: " + type));
    }

    public List<Application> getAllApplications() {
        return applicationRepository.findAll();
    }

    @Transactional
    public void createIfAbsent(AppType type) {
        if (applicationRepository.findByType(type).isEmpty()) {
            Application app = new Application();
            app.setType(type);
            applicationRepository.save(app);
        }
    }
}
