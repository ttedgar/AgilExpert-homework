package com.edi.backend.service;

import com.edi.backend.domain.AppType;
import com.edi.backend.domain.Application;
import com.edi.backend.repository.ApplicationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceTest {

    @Mock ApplicationRepository applicationRepository;

    @InjectMocks ApplicationService applicationService;

    @Test
    void getByType_returnsApplication() {
        Application app = new Application();
        app.setType(AppType.PAINT);
        when(applicationRepository.findByType(AppType.PAINT)).thenReturn(Optional.of(app));

        Application result = applicationService.getByType(AppType.PAINT);

        assertThat(result).isSameAs(app);
        assertThat(result.getType()).isEqualTo(AppType.PAINT);
    }

    @Test
    void getByType_throwsWhenNotFound() {
        when(applicationRepository.findByType(AppType.OPENMAP)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> applicationService.getByType(AppType.OPENMAP))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Application not found");
    }

    @Test
    void getAllApplications_returnsAllApplications() {
        List<Application> apps = List.of(new Application(), new Application());
        when(applicationRepository.findAll()).thenReturn(apps);

        assertThat(applicationService.getAllApplications()).isEqualTo(apps);
    }
}
