package com.edi.backend.controller;

import com.edi.backend.domain.AppType;
import com.edi.backend.service.ApplicationService;
import com.edi.backend.service.MenuService;
import com.edi.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class MenuController {

    private final UserService userService;
    private final MenuService menuService;
    private final ApplicationService applicationService;

    @GetMapping("/users/{id}/desktop")
    public String desktop(@PathVariable String id,
                          @RequestParam(required = false) String menuId,
                          Model model) {
        var user = userService.getById(id);
        var resolvedMenuId = menuId != null ? menuId : user.getMainMenu().getId();
        var menu = menuService.getMenuById(resolvedMenuId);
        model.addAttribute("user", user);
        model.addAttribute("menu", menu);
        return "users/desktop";
    }

    @GetMapping("/users/{id}/menu/edit")
    public String editMenu(@PathVariable String id,
                           @RequestParam(required = false) String menuId,
                           Model model) {
        var user = userService.getById(id);
        var menu = menuId != null ? menuService.getMenuById(menuId) : user.getMainMenu();
        model.addAttribute("user", user);
        model.addAttribute("menu", menu);
        model.addAttribute("applications", applicationService.getAllApplications());
        return "menu/edit";
    }

    @PostMapping("/users/{id}/menu/items")
    public String addItem(@PathVariable String id,
                          @RequestParam String menuId,
                          @RequestParam String type,
                          @RequestParam(required = false) String appType,
                          @RequestParam(required = false) String label,
                          @RequestParam(required = false) String subMenuName,
                          Model model) {
        var user = userService.getById(id);
        var menu = menuService.getMenuById(menuId);
        try {
            if ("app".equals(type)) {
                var app = applicationService.getByType(AppType.valueOf(appType));
                menuService.addAppShortcut(menu, app, label);
            } else {
                menuService.createSubMenu(menu, subMenuName);
            }
        } catch (IllegalArgumentException e) {
            model.addAttribute("user", user);
            model.addAttribute("menu", menu);
            model.addAttribute("applications", applicationService.getAllApplications());
            model.addAttribute("error", e.getMessage());
            return "menu/edit";
        }
        return "redirect:/users/" + id + "/menu/edit?menuId=" + menuId;
    }

    @PostMapping("/users/{id}/menu/items/{itemId}/delete")
    public String deleteItem(@PathVariable String id,
                             @PathVariable String itemId,
                             @RequestParam String menuId) {
        menuService.deleteMenuItem(itemId);
        return "redirect:/users/" + id + "/menu/edit?menuId=" + menuId;
    }
}
