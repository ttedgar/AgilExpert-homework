package com.edi.backend;

import com.edi.backend.domain.AppType;
import com.edi.backend.domain.Application;
import com.edi.backend.repository.ApplicationRepository;
import com.edi.backend.repository.UserRepository;
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

    private final ApplicationRepository applicationRepository;
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
            if (applicationRepository.findByType(type).isEmpty()) {
                Application app = new Application();
                app.setType(type);
                applicationRepository.save(app);
            }
        }
    }

    private void seedDeveloper() {
        if (!userRepository.existsByName("Edi")) {
            var user = userService.createUser("Edi");
            menuService.addAppShortcut(user.getMainMenu(),
                    applicationRepository.findByType(AppType.MINESWEEPER).orElseThrow(),
                    "Minesweeper");
            menuService.addAppShortcut(user.getMainMenu(),
                    applicationRepository.findByType(AppType.PAINT).orElseThrow(),
                    "Paint");
        }
    }
}
