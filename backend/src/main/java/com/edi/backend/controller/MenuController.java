package com.edi.backend.controller;

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
        model.addAttribute("user", user);
        model.addAttribute("menu", menuService.resolveMenu(user, menuId));
        return "users/desktop";
    }

    @GetMapping("/users/{id}/menu/edit")
    public String editMenu(@PathVariable String id,
                           @RequestParam(required = false) String menuId,
                           Model model) {
        var user = userService.getById(id);
        model.addAttribute("user", user);
        model.addAttribute("menu", menuService.resolveMenu(user, menuId));
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
        try {
            menuService.addItem(menuId, type, appType, label, subMenuName);
        } catch (IllegalArgumentException e) {
            var user = userService.getById(id);
            model.addAttribute("user", user);
            model.addAttribute("menu", menuService.getMenuById(menuId));
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
