package com.edi.backend.service;

import com.edi.backend.domain.AppType;
import com.edi.backend.domain.Application;
import com.edi.backend.domain.Menu;
import com.edi.backend.domain.MenuItem;
import com.edi.backend.domain.User;
import com.edi.backend.repository.MenuItemRepository;
import com.edi.backend.repository.MenuRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {

    @Mock MenuRepository menuRepository;
    @Mock MenuItemRepository menuItemRepository;

    @InjectMocks MenuService menuService;

    @Test
    void addAppShortcut_savesItemWithCorrectFieldsAndPosition() {
        Menu menu = new Menu();
        Application app = appWithId("app1", AppType.PAINT);
        when(menuItemRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        menuService.addAppShortcut(menu, app, "Paint");

        ArgumentCaptor<MenuItem> captor = ArgumentCaptor.forClass(MenuItem.class);
        verify(menuItemRepository).save(captor.capture());
        MenuItem saved = captor.getValue();

        assertThat(saved.getName()).isEqualTo("Paint");
        assertThat(saved.getMenu()).isSameAs(menu);
        assertThat(saved.getApplication()).isSameAs(app);
        assertThat(saved.getPosition()).isEqualTo(0);
    }

    @Test
    void addAppShortcut_positionEqualsCurrentItemCount() {
        Application existingApp = appWithId("app1", AppType.MINESWEEPER);
        MenuItem existingItem = new MenuItem();
        existingItem.setApplication(existingApp);

        Menu menu = new Menu();
        menu.getItems().add(existingItem);

        Application newApp = appWithId("app2", AppType.PAINT);
        when(menuItemRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        menuService.addAppShortcut(menu, newApp, "Paint");

        ArgumentCaptor<MenuItem> captor = ArgumentCaptor.forClass(MenuItem.class);
        verify(menuItemRepository).save(captor.capture());
        assertThat(captor.getValue().getPosition()).isEqualTo(1);
    }

    @Test
    void addAppShortcut_throwsWhenApplicationAlreadyInMenu() {
        Application app = appWithId("app1", AppType.PAINT);
        MenuItem existing = new MenuItem();
        existing.setApplication(app);

        Menu menu = new Menu();
        menu.getItems().add(existing);

        assertThatThrownBy(() -> menuService.addAppShortcut(menu, app, "Paint"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already in this menu");
    }

    @Test
    void createSubMenu_savesSubMenuAndLinkItemInParent() {
        User owner = new User();
        Menu parentMenu = new Menu();
        parentMenu.setOwner(owner);

        Menu savedSubMenu = new Menu();
        savedSubMenu.setId("sub1");
        when(menuRepository.save(any())).thenReturn(savedSubMenu);
        when(menuItemRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Menu result = menuService.createSubMenu(parentMenu, "Games");

        assertThat(result).isSameAs(savedSubMenu);

        ArgumentCaptor<Menu> menuCaptor = ArgumentCaptor.forClass(Menu.class);
        verify(menuRepository).save(menuCaptor.capture());
        Menu toSave = menuCaptor.getValue();
        assertThat(toSave.getName()).isEqualTo("Games");
        assertThat(toSave.getOwner()).isSameAs(owner);
        assertThat(toSave.getParentMenu()).isSameAs(parentMenu);

        ArgumentCaptor<MenuItem> itemCaptor = ArgumentCaptor.forClass(MenuItem.class);
        verify(menuItemRepository).save(itemCaptor.capture());
        MenuItem link = itemCaptor.getValue();
        assertThat(link.getName()).isEqualTo("Games");
        assertThat(link.getMenu()).isSameAs(parentMenu);
        assertThat(link.getSubMenu()).isSameAs(savedSubMenu);
    }

    @Test
    void createSubMenu_throwsWhenParentIsAlreadyASubfolder() {
        Menu grandparent = new Menu();
        Menu parent = new Menu();
        parent.setParentMenu(grandparent);

        assertThatThrownBy(() -> menuService.createSubMenu(parent, "Nested"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be nested");
    }

    @Test
    void deleteMenuItem_callsDeleteByIdOnRepository() {
        menuService.deleteMenuItem("item1");

        verify(menuItemRepository).deleteById("item1");
    }

    @Test
    void getMenuById_returnsMenu() {
        Menu menu = new Menu();
        when(menuRepository.findByIdWithItems("m1")).thenReturn(Optional.of(menu));

        assertThat(menuService.getMenuById("m1")).isSameAs(menu);
    }

    @Test
    void getMenuById_throwsWhenNotFound() {
        when(menuRepository.findByIdWithItems("m99")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> menuService.getMenuById("m99"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Menu not found");
    }

    private Application appWithId(String id, AppType type) {
        Application app = new Application();
        app.setId(id);
        app.setType(type);
        return app;
    }
}
