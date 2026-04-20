package com.edi.backend.repository;

import com.edi.backend.domain.AppType;
import com.edi.backend.domain.Application;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, String> {
    Optional<Application> findByType(AppType type);
}
