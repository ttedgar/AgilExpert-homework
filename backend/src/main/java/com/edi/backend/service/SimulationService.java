package com.edi.backend.service;

import com.edi.backend.domain.AppType;
import com.edi.backend.domain.Application;
import com.edi.backend.domain.Menu;
import com.edi.backend.domain.User;
import com.edi.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class SimulationService {

    private final UserService userService;
    private final MenuService menuService;
    private final ApplicationService applicationService;
    private final UserRepository userRepository;

    public void run() {
        if (userRepository.existsByName("Anna")) return;

        Application minesweeper = applicationService.getByType(AppType.MINESWEEPER);
        Application openmap     = applicationService.getByType(AppType.OPENMAP);
        Application paint       = applicationService.getByType(AppType.PAINT);
        Application contacts    = applicationService.getByType(AppType.CONTACTS);

        createSimUser("Anna",   "Morning Blush", "#FFE5E5", "Rose",      "#FF2D55", contacts, paint);
        createSimUser("Péter",  "Ocean",         "#E0F0FF", "Sky Blue",  "#007AFF", openmap, contacts);
        createSimUser("Bence",  "Sunshine",      "#FFF8E0", "Orange",    "#FF9500", minesweeper, paint);

        User eszter = createSimUser("Eszter", "Mint",     "#E0FFE8", "Green",     "#34C759", paint);
        Menu gamesFolder = menuService.createSubMenu(menuService.getMenuById(eszter.getMainMenu().getId()), "Games");
        menuService.addAppShortcut(gamesFolder, minesweeper, minesweeper.getType().label());

    }

    private User createSimUser(String name,
                               String wallpaperName,
                               String wallpaperColor,
                               String themeName,
                               String themeColor,
                               Application... apps) {
        User user = userService.createUser(name);

        var wallpaper = userService.addWallpaper(user.getId(), wallpaperName, wallpaperColor);
        userService.activateWallpaper(user.getId(), wallpaper.getId());

        var theme = userService.addTheme(user.getId(), themeName, themeColor);
        userService.activateTheme(user.getId(), theme.getId());

        for (Application app : apps) {
            Menu menu = menuService.getMenuById(user.getMainMenu().getId());
            menuService.addAppShortcut(menu, app, app.getType().label());
        }

        return user;
    }
}
