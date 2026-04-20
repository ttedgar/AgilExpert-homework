package com.edi.backend.service;

import com.edi.backend.domain.Application;
import com.edi.backend.domain.Menu;
import com.edi.backend.domain.MenuItem;
import com.edi.backend.repository.MenuItemRepository;
import com.edi.backend.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;
    private final MenuItemRepository menuItemRepository;

    public MenuItem addAppShortcut(Menu menu, Application application, String label) {
        MenuItem item = new MenuItem();
        item.setName(label);
        item.setMenu(menu);
        item.setApplication(application);
        item.setPosition(menu.getItems().size());
        return menuItemRepository.save(item);
    }

    public Menu createSubMenu(Menu parentMenu, String name) {
        Menu subMenu = new Menu();
        subMenu.setName(name);
        subMenu.setOwner(parentMenu.getOwner());
        subMenu.setParentMenu(parentMenu);
        subMenu = menuRepository.save(subMenu);

        MenuItem link = new MenuItem();
        link.setName(name);
        link.setMenu(parentMenu);
        link.setSubMenu(subMenu);
        link.setPosition(parentMenu.getItems().size());
        menuItemRepository.save(link);

        return subMenu;
    }

    public void deleteMenuItem(String menuItemId) {
        menuItemRepository.deleteById(menuItemId);
    }

    @Transactional(readOnly = true)
    public Menu getMenuById(String id) {
        return menuRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Menu not found: " + id));
    }
}
