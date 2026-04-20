package com.edi.backend.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ApplicationController {

    @GetMapping("/app/{type}")
    public String launch(@PathVariable String type,
                         @RequestParam(required = false) String userId,
                         Model model) {
        model.addAttribute("userId", userId);
        return "apps/" + type.toLowerCase();
    }
}
