package com.edi.backend.cli.handler;

import com.edi.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Map;

@Profile("dev")
@Component
@RequiredArgsConstructor
public class CreateUserHandler implements ToolHandler {

    private final UserService userService;

    @Override
    public String name() {
        return "create_user";
    }

    @Override
    public String toolDefinition() {
        return """
                {"type":"function","function":{
                  "name":"create_user",
                  "description":"Create a new user",
                  "parameters":{"type":"object",
                    "properties":{"name":{"type":"string","description":"The user's name"}},
                    "required":["name"]}}}""";
    }

    @Override
    public void execute(Map<String, Object> args) {
        var user = userService.createUser((String) args.get("name"));
        System.out.println("Created user: " + user.getName());
    }
}
