package com.edi.backend.service;

import com.edi.backend.domain.Menu;
import com.edi.backend.domain.Theme;
import com.edi.backend.domain.User;
import com.edi.backend.domain.Wallpaper;
import com.edi.backend.repository.MenuRepository;
import com.edi.backend.repository.ThemeRepository;
import com.edi.backend.repository.UserRepository;
import com.edi.backend.repository.WallpaperRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final MenuRepository menuRepository;
    private final WallpaperRepository wallpaperRepository;
    private final ThemeRepository themeRepository;

    public User createUser(String name) {
        Menu mainMenu = new Menu();
        mainMenu.setName("Main Menu");

        User user = new User();
        user.setName(name);
        user.setMainMenu(mainMenu);
        mainMenu.setOwner(user);

        return userRepository.save(user);
    }

    public User updateName(String id, String newName) {
        User user = getById(id);
        user.setName(newName);
        return userRepository.save(user);
    }

    public void deleteUser(String id) {
        User user = getById(id);
        menuRepository.deleteByOwnerAndParentMenuIsNotNull(user);
        userRepository.deleteById(id);
    }

    public Wallpaper addWallpaper(String userId, String name, String color) {
        User user = getById(userId);
        Wallpaper wallpaper = new Wallpaper();
        wallpaper.setName(name);
        wallpaper.setColor(color);
        wallpaper.setOwner(user);
        return wallpaperRepository.save(wallpaper);
    }

    public void activateWallpaper(String userId, String wallpaperId) {
        User user = getById(userId);
        Wallpaper wallpaper = wallpaperRepository.findById(wallpaperId)
                .orElseThrow(() -> new IllegalArgumentException("Wallpaper not found: " + wallpaperId));
        user.setActiveWallpaper(wallpaper);
        userRepository.save(user);
    }

    public Theme addTheme(String userId, String name, String color) {
        User user = getById(userId);
        Theme theme = new Theme();
        theme.setName(name);
        theme.setColor(color);
        theme.setOwner(user);
        return themeRepository.save(theme);
    }

    public void activateTheme(String userId, String themeId) {
        User user = getById(userId);
        Theme theme = themeRepository.findById(themeId)
                .orElseThrow(() -> new IllegalArgumentException("Theme not found: " + themeId));
        user.setActiveTheme(theme);
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User getById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
    }

    @Transactional(readOnly = true)
    public User getByIdWithAppearance(String id) {
        User user = getById(id);
        Hibernate.initialize(user.getWallpapers());
        Hibernate.initialize(user.getThemes());
        return user;
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
