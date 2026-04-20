package com.edi.backend.cli;

import com.edi.backend.domain.AppType;
import com.edi.backend.service.ApplicationService;
import com.edi.backend.service.MenuService;
import com.edi.backend.service.SimulationService;
import com.edi.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
@Order(2)
@RequiredArgsConstructor
public class CliPromptRunner implements ApplicationRunner {

    private final LlmClient llmClient;
    private final UserService userService;
    private final MenuService menuService;
    private final ApplicationService applicationService;
    private final SimulationService simulationService;

    @Override
    public void run(ApplicationArguments args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n╔══════════════════════════════╗");
        System.out.println("║       SmartOS CLI ready      ║");
        System.out.println("╚══════════════════════════════╝");
        System.out.println("Web UI : http://localhost:8080");
        System.out.println("Type a command, or 'exit' to quit.\n");

        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) continue;
            if (input.equalsIgnoreCase("exit")) break;

            System.out.println("...");
            llmClient.call(input).ifPresentOrElse(
                    this::execute,
                    () -> System.out.println("I didn't understand that. Try something like: 'open the map for Péter'")
            );
        }
    }

    private void openBrowser(String url) {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                Runtime.getRuntime().exec(new String[]{"rundll32", "url.dll,FileProtocolHandler", url});
            } else if (os.contains("mac")) {
                Runtime.getRuntime().exec(new String[]{"open", url});
            } else {
                // Linux / WSL2 — try cmd.exe first (WSL2), fall back to xdg-open
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

    private void execute(LlmClient.ToolCall toolCall) {
        try {
            switch (toolCall.name()) {
                case "list_users" -> {
                    var users = userService.getAllUsers();
                    if (users.isEmpty()) {
                        System.out.println("No users found.");
                    } else {
                        System.out.println("Users:");
                        users.forEach(u -> System.out.println("  - " + u.getName()));
                    }
                }
                case "create_user" -> {
                    String name = (String) toolCall.arguments().get("name");
                    var user = userService.createUser(name);
                    System.out.println("Created user: " + user.getName());
                }
                case "run_simulation" -> {
                    simulationService.run();
                    System.out.println("Demo data loaded: Anna, Péter, Eszter, Bence");
                }
                case "open_app" -> {
                    String userName = (String) toolCall.arguments().get("user_name");
                    AppType appType = AppType.valueOf((String) toolCall.arguments().get("app_type"));
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
                        break;
                    }
                    String url = "http://localhost:8080/app/%s?userId=%s"
                            .formatted(appType.name().toLowerCase(), user.getId());
                    System.out.printf("Opening %s for %s%n", appType.label(), user.getName());
                    System.out.println("→ " + url);
                    openBrowser(url);
                }
                case "add_app_to_menu" -> {
                    String userName = (String) toolCall.arguments().get("user_name");
                    AppType appType = AppType.valueOf((String) toolCall.arguments().get("app_type"));
                    var user = userService.getAllUsers().stream()
                            .filter(u -> u.getName().equalsIgnoreCase(userName))
                            .findFirst()
                            .orElseThrow(() -> new IllegalArgumentException("User not found: " + userName));
                    var app  = applicationService.getByType(appType);
                    var menu = menuService.getMenuById(user.getMainMenu().getId());
                    menuService.addAppShortcut(menu, app, appType.label());
                    System.out.printf("Added %s to %s's menu%n", appType.label(), user.getName());
                }
                default -> System.out.println("Unknown tool: " + toolCall.name());
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
