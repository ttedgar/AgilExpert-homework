package com.edi.backend.cli.handler;

import com.edi.backend.domain.AppType;
import com.edi.backend.service.ApplicationService;
import com.edi.backend.service.MenuService;
import com.edi.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Map;

@Profile("dev")
@Component
@RequiredArgsConstructor
public class AddAppToMenuHandler implements ToolHandler {

    private final UserService userService;
    private final MenuService menuService;
    private final ApplicationService applicationService;

    @Override
    public String name() {
        return "add_app_to_menu";
    }

    @Override
    public String toolDefinition() {
        return """
                {"type":"function","function":{
                  "name":"add_app_to_menu",
                  "description":"Add an application shortcut to a user's main menu",
                  "parameters":{"type":"object",
                    "properties":{
                      "user_name":{"type":"string","description":"The user's name"},
                      "app_type":{"type":"string","enum":["MINESWEEPER","OPENMAP","PAINT","CONTACTS"]}},
                    "required":["user_name","app_type"]}}}""";
    }

    @Override
    public void execute(Map<String, Object> args) {
        String userName = (String) args.get("user_name");
        AppType appType = AppType.valueOf((String) args.get("app_type"));

        var user = userService.getAllUsers().stream()
                .filter(u -> u.getName().equalsIgnoreCase(userName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userName));

        var app  = applicationService.getByType(appType);
        var menu = menuService.getMenuById(user.getMainMenu().getId());
        menuService.addAppShortcut(menu, app, appType.label());
        System.out.printf("Added %s to %s's menu%n", appType.label(), user.getName());
    }
}
