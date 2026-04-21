package com.edi.backend.service;

import com.edi.backend.domain.AppType;
import com.edi.backend.domain.Application;
import com.edi.backend.domain.Menu;
import com.edi.backend.domain.Theme;
import com.edi.backend.domain.User;
import com.edi.backend.domain.Wallpaper;
import com.edi.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SimulationServiceTest {

    @Mock UserService userService;
    @Mock MenuService menuService;
    @Mock ApplicationService applicationService;
    @Mock UserRepository userRepository;

    @InjectMocks SimulationService simulationService;

    private Application minesweeper;
    private Application openmap;
    private Application paint;
    private Application contacts;

    @BeforeEach
    void setUpApplications() {
        minesweeper = appOf("app-ms", AppType.MINESWEEPER);
        openmap     = appOf("app-om", AppType.OPENMAP);
        paint       = appOf("app-pt", AppType.PAINT);
        contacts    = appOf("app-ct", AppType.CONTACTS);
    }

    @Test
    void run_doesNothingWhenAnnaAlreadyExists() {
        when(userRepository.existsByName("Anna")).thenReturn(true);

        simulationService.run();

        verify(userService, never()).createUser(anyString());
    }

    @Test
    void run_createsFourUsersWithCorrectNames() {
        when(userRepository.existsByName("Anna")).thenReturn(false);

        stubApplicationLookups();
        stubUserServiceCalls();
        stubMenuServiceCalls();

        simulationService.run();

        verify(userService).createUser("Anna");
        verify(userService).createUser("Péter");
        verify(userService).createUser("Bence");
        verify(userService).createUser("Eszter");
        verify(userService, times(4)).createUser(anyString());
    }

    @Test
    void run_addsWallpaperAndThemeForEachUser() {
        when(userRepository.existsByName("Anna")).thenReturn(false);

        stubApplicationLookups();
        stubUserServiceCalls();
        stubMenuServiceCalls();

        simulationService.run();

        verify(userService, times(4)).addWallpaper(anyString(), anyString(), anyString());
        verify(userService, times(4)).activateWallpaper(anyString(), anyString());
        verify(userService, times(4)).addTheme(anyString(), anyString(), anyString());
        verify(userService, times(4)).activateTheme(anyString(), anyString());
    }

    @Test
    void run_createsSubFolderAndAddsShortcutForEszter() {
        when(userRepository.existsByName("Anna")).thenReturn(false);

        stubApplicationLookups();

        Menu eszterMainMenu = emptyMenu("eszter-menu");
        Menu gamesFolder = emptyMenu("games-folder");

        // Return a distinct user for Eszter to track her main menu id
        User eszterUser = userWithMenu("eszter", eszterMainMenu);
        User genericUser = userWithMenu("generic", emptyMenu("generic-menu"));

        when(userService.createUser("Anna")).thenReturn(genericUser);
        when(userService.createUser("Péter")).thenReturn(genericUser);
        when(userService.createUser("Bence")).thenReturn(genericUser);
        when(userService.createUser("Eszter")).thenReturn(eszterUser);

        Wallpaper wallpaper = wallpaperWithId("w1");
        Theme theme = themeWithId("t1");
        when(userService.addWallpaper(anyString(), anyString(), anyString())).thenReturn(wallpaper);
        when(userService.addTheme(anyString(), anyString(), anyString())).thenReturn(theme);

        when(menuService.getMenuById("generic-menu")).thenReturn(emptyMenu("generic-menu"));
        when(menuService.getMenuById("eszter-menu")).thenReturn(eszterMainMenu);
        when(menuService.createSubMenu(any(), anyString())).thenReturn(gamesFolder);
        when(menuService.addAppShortcut(any(), any(), anyString())).thenReturn(null);

        simulationService.run();

        verify(menuService).createSubMenu(eszterMainMenu, "Games");
        verify(menuService).addAppShortcut(gamesFolder, minesweeper, AppType.MINESWEEPER.label());
    }

    // --- helpers ---

    private void stubApplicationLookups() {
        when(applicationService.getByType(AppType.MINESWEEPER)).thenReturn(minesweeper);
        when(applicationService.getByType(AppType.OPENMAP)).thenReturn(openmap);
        when(applicationService.getByType(AppType.PAINT)).thenReturn(paint);
        when(applicationService.getByType(AppType.CONTACTS)).thenReturn(contacts);
    }

    private void stubUserServiceCalls() {
        User user = userWithMenu("u1", emptyMenu("m1"));
        when(userService.createUser(anyString())).thenReturn(user);
        when(userService.addWallpaper(anyString(), anyString(), anyString())).thenReturn(wallpaperWithId("w1"));
        when(userService.addTheme(anyString(), anyString(), anyString())).thenReturn(themeWithId("t1"));
    }

    private void stubMenuServiceCalls() {
        when(menuService.getMenuById(anyString())).thenReturn(emptyMenu("m1"));
        when(menuService.addAppShortcut(any(), any(), anyString())).thenReturn(null);
        when(menuService.createSubMenu(any(), anyString())).thenReturn(emptyMenu("sub1"));
    }

    private Application appOf(String id, AppType type) {
        Application app = new Application();
        app.setId(id);
        app.setType(type);
        return app;
    }

    private User userWithMenu(String userId, Menu mainMenu) {
        User user = new User();
        user.setId(userId);
        user.setMainMenu(mainMenu);
        return user;
    }

    private Menu emptyMenu(String menuId) {
        Menu menu = new Menu();
        menu.setId(menuId);
        return menu;
    }

    private Wallpaper wallpaperWithId(String id) {
        Wallpaper w = new Wallpaper();
        w.setId(id);
        return w;
    }

    private Theme themeWithId(String id) {
        Theme t = new Theme();
        t.setId(id);
        return t;
    }
}
