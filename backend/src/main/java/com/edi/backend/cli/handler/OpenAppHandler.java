package com.edi.backend.cli.handler;

import com.edi.backend.domain.AppType;
import com.edi.backend.service.MenuService;
import com.edi.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Map;

@Profile("dev")
@Component
@RequiredArgsConstructor
public class OpenAppHandler implements ToolHandler {

    private final UserService userService;
    private final MenuService menuService;

    @Override
    public String name() {
        return "open_app";
    }

    @Override
    public String toolDefinition() {
        return """
                {"type":"function","function":{
                  "name":"open_app",
                  "description":"Open an application for a specific user",
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

        var menu = menuService.getMenuById(user.getMainMenu().getId());
        boolean hasApp = menu.getItems().stream()
                .filter(i -> i.getApplication() != null)
                .anyMatch(i -> i.getApplication().getType() == appType);

        if (!hasApp) {
            System.out.printf("%s does not have %s in their menu.%n", user.getName(), appType.label());
            System.out.printf("Use 'add %s to %s's menu' first.%n", appType.label(), user.getName());
            return;
        }

        String url = "http://localhost:8080/app/%s?userId=%s"
                .formatted(appType.name().toLowerCase(), user.getId());
        System.out.printf("Opening %s for %s%n", appType.label(), user.getName());
        System.out.println("→ " + url);
        openBrowser(url);
    }

    private void openBrowser(String url) {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                Runtime.getRuntime().exec(new String[]{"rundll32", "url.dll,FileProtocolHandler", url});
            } else if (os.contains("mac")) {
                Runtime.getRuntime().exec(new String[]{"open", url});
            } else {
                try {
                    Runtime.getRuntime().exec(new String[]{"cmd.exe", "/c", "start", url});
                } catch (Exception e) {
                    Runtime.getRuntime().exec(new String[]{"xdg-open", url});
                }
            }
        } catch (Exception e) {
            System.out.println("Could not open browser: " + e.getMessage());
        }
    }
}
