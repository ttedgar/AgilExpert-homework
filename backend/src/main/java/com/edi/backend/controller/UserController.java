package com.edi.backend.controller;

import com.edi.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "users/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("userId", null);
        model.addAttribute("userName", "");
        return "users/form";
    }

    @PostMapping
    public String create(@RequestParam String name) {
        userService.createUser(name);
        return "redirect:/users";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable String id, Model model) {
        var user = userService.getById(id);
        model.addAttribute("userId", user.getId());
        model.addAttribute("userName", user.getName());
        return "users/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable String id, @RequestParam String name) {
        userService.updateName(id, name);
        return "redirect:/users";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable String id) {
        userService.deleteUser(id);
        return "redirect:/users";
    }
}
