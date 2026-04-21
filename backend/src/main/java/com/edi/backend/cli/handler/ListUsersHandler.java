package com.edi.backend.cli.handler;

import com.edi.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Map;

@Profile("dev")
@Component
@RequiredArgsConstructor
public class ListUsersHandler implements ToolHandler {

    private final UserService userService;

    @Override
    public String name() {
        return "list_users";
    }

    @Override
    public String toolDefinition() {
        return """
                {"type":"function","function":{
                  "name":"list_users",
                  "description":"List all users in the system",
                  "parameters":{"type":"object","properties":{}}}}""";
    }

    @Override
    public void execute(Map<String, Object> args) {
        var users = userService.getAllUsers();
        if (users.isEmpty()) {
            System.out.println("No users found.");
        } else {
            System.out.println("Users:");
            users.forEach(u -> System.out.println("  - " + u.getName()));
        }
    }
}
