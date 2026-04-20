package com.edi.backend.web;

import com.edi.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/users/{id}/appearance")
@RequiredArgsConstructor
public class AppearanceController {

    private final UserService userService;

    @GetMapping
    public String appearance(@PathVariable String id, Model model) {
        model.addAttribute("user", userService.getByIdWithAppearance(id));
        return "users/appearance";
    }

    @PostMapping("/wallpapers")
    public String addWallpaper(@PathVariable String id,
                               @RequestParam String name,
                               @RequestParam String color) {
        userService.addWallpaper(id, name, color);
        return "redirect:/users/" + id + "/appearance";
    }

    @PostMapping("/wallpapers/{wpId}/activate")
    public String activateWallpaper(@PathVariable String id, @PathVariable String wpId) {
        userService.activateWallpaper(id, wpId);
        return "redirect:/users/" + id + "/appearance";
    }

    @PostMapping("/themes")
    public String addTheme(@PathVariable String id,
                           @RequestParam String name,
                           @RequestParam String color) {
        userService.addTheme(id, name, color);
        return "redirect:/users/" + id + "/appearance";
    }

    @PostMapping("/themes/{themeId}/activate")
    public String activateTheme(@PathVariable String id, @PathVariable String themeId) {
        userService.activateTheme(id, themeId);
        return "redirect:/users/" + id + "/appearance";
    }
}
