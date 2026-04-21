package com.edi.backend.service;

import com.edi.backend.domain.Theme;
import com.edi.backend.domain.User;
import com.edi.backend.domain.Wallpaper;
import com.edi.backend.repository.MenuRepository;
import com.edi.backend.repository.ThemeRepository;
import com.edi.backend.repository.UserRepository;
import com.edi.backend.repository.WallpaperRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock UserRepository userRepository;
    @Mock MenuRepository menuRepository;
    @Mock WallpaperRepository wallpaperRepository;
    @Mock ThemeRepository themeRepository;

    @InjectMocks UserService userService;

    @Test
    void createUser_savesUserWithNameAndMainMenu() {
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        userService.createUser("Alice");

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User saved = captor.getValue();

        assertThat(saved.getName()).isEqualTo("Alice");
        assertThat(saved.getMainMenu()).isNotNull();
        assertThat(saved.getMainMenu().getName()).isEqualTo("Main Menu");
        assertThat(saved.getMainMenu().getOwner()).isSameAs(saved);
    }

    @Test
    void updateName_updatesNameAndSaves() {
        User user = userWithId("u1");
        when(userRepository.findById("u1")).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        User result = userService.updateName("u1", "Bob");

        assertThat(result.getName()).isEqualTo("Bob");
        verify(userRepository).save(user);
    }

    @Test
    void updateName_throwsWhenUserNotFound() {
        when(userRepository.findById("u99")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateName("u99", "Bob"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void deleteUser_deletesSubmenusAndThenUser() {
        User user = userWithId("u1");
        when(userRepository.findById("u1")).thenReturn(Optional.of(user));

        userService.deleteUser("u1");

        verify(menuRepository).deleteByOwnerAndParentMenuIsNotNull(user);
        verify(userRepository).deleteById("u1");
    }

    @Test
    void deleteUser_throwsWhenUserNotFound() {
        when(userRepository.findById("u99")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.deleteUser("u99"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void addWallpaper_createsWallpaperWithCorrectFieldsAndOwner() {
        User user = userWithId("u1");
        when(userRepository.findById("u1")).thenReturn(Optional.of(user));
        when(wallpaperRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        userService.addWallpaper("u1", "Ocean", "#E0F0FF");

        ArgumentCaptor<Wallpaper> captor = ArgumentCaptor.forClass(Wallpaper.class);
        verify(wallpaperRepository).save(captor.capture());
        Wallpaper saved = captor.getValue();

        assertThat(saved.getName()).isEqualTo("Ocean");
        assertThat(saved.getColor()).isEqualTo("#E0F0FF");
        assertThat(saved.getOwner()).isSameAs(user);
    }

    @Test
    void activateWallpaper_setsActiveWallpaperAndSavesUser() {
        User user = userWithId("u1");
        Wallpaper wallpaper = new Wallpaper();
        when(userRepository.findById("u1")).thenReturn(Optional.of(user));
        when(wallpaperRepository.findById("w1")).thenReturn(Optional.of(wallpaper));

        userService.activateWallpaper("u1", "w1");

        assertThat(user.getActiveWallpaper()).isSameAs(wallpaper);
        verify(userRepository).save(user);
    }

    @Test
    void activateWallpaper_throwsWhenWallpaperNotFound() {
        User user = userWithId("u1");
        when(userRepository.findById("u1")).thenReturn(Optional.of(user));
        when(wallpaperRepository.findById("w99")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.activateWallpaper("u1", "w99"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Wallpaper not found");
    }

    @Test
    void addTheme_createsThemeWithCorrectFieldsAndOwner() {
        User user = userWithId("u1");
        when(userRepository.findById("u1")).thenReturn(Optional.of(user));
        when(themeRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        userService.addTheme("u1", "Rose", "#FF2D55");

        ArgumentCaptor<Theme> captor = ArgumentCaptor.forClass(Theme.class);
        verify(themeRepository).save(captor.capture());
        Theme saved = captor.getValue();

        assertThat(saved.getName()).isEqualTo("Rose");
        assertThat(saved.getColor()).isEqualTo("#FF2D55");
        assertThat(saved.getOwner()).isSameAs(user);
    }

    @Test
    void activateTheme_setsActiveThemeAndSavesUser() {
        User user = userWithId("u1");
        Theme theme = new Theme();
        when(userRepository.findById("u1")).thenReturn(Optional.of(user));
        when(themeRepository.findById("t1")).thenReturn(Optional.of(theme));

        userService.activateTheme("u1", "t1");

        assertThat(user.getActiveTheme()).isSameAs(theme);
        verify(userRepository).save(user);
    }

    @Test
    void activateTheme_throwsWhenThemeNotFound() {
        User user = userWithId("u1");
        when(userRepository.findById("u1")).thenReturn(Optional.of(user));
        when(themeRepository.findById("t99")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.activateTheme("u1", "t99"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Theme not found");
    }

    @Test
    void getById_returnsUser() {
        User user = userWithId("u1");
        when(userRepository.findById("u1")).thenReturn(Optional.of(user));

        assertThat(userService.getById("u1")).isSameAs(user);
    }

    @Test
    void getById_throwsWhenNotFound() {
        when(userRepository.findById("u99")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getById("u99"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void getByIdWithAppearance_returnsUser() {
        User user = userWithId("u1");
        when(userRepository.findById("u1")).thenReturn(Optional.of(user));

        User result = userService.getByIdWithAppearance("u1");

        assertThat(result).isSameAs(user);
    }

    @Test
    void getAllUsers_returnsAllUsers() {
        List<User> users = List.of(new User(), new User());
        when(userRepository.findAll()).thenReturn(users);

        assertThat(userService.getAllUsers()).isEqualTo(users);
    }

    private User userWithId(String id) {
        User user = new User();
        user.setId(id);
        return user;
    }
}
