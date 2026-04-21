package com.edi.backend;

import com.edi.backend.domain.AppType;
import com.edi.backend.repository.UserRepository;
import com.edi.backend.service.ApplicationService;
import com.edi.backend.service.MenuService;
import com.edi.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
@RequiredArgsConstructor
public class DataSeeder implements ApplicationRunner {

    private final ApplicationService applicationService;
    private final UserRepository userRepository;
    private final UserService userService;
    private final MenuService menuService;

    @Override
    public void run(ApplicationArguments args) {
        seedApplications();
        seedDeveloper();
    }

    private void seedApplications() {
        for (AppType type : AppType.values()) {
            applicationService.createIfAbsent(type);
        }
    }

    private void seedDeveloper() {
        if (!userRepository.existsByName("Edi")) {
            var user = userService.createUser("Edi");
            menuService.addAppShortcut(user.getMainMenu(),
                    applicationService.getByType(AppType.MINESWEEPER), "Minesweeper");
            menuService.addAppShortcut(user.getMainMenu(),
                    applicationService.getByType(AppType.PAINT), "Paint");
        }
    }
}
