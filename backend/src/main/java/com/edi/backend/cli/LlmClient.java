package com.edi.backend.cli;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;
import java.util.Optional;

@Component
public class LlmClient {

    @Value("${openrouter.api-key}")
    private String apiKey;

    private final ObjectMapper mapper = new ObjectMapper();

    private static final String URL   = "https://openrouter.ai/api/v1/chat/completions";
    private static final String MODEL = "openrouter/free";

    private static final String TOOLS = """
            [
              {"type":"function","function":{
                "name":"list_users",
                "description":"List all users in the system",
                "parameters":{"type":"object","properties":{}}}},
              {"type":"function","function":{
                "name":"create_user",
                "description":"Create a new user",
                "parameters":{"type":"object","properties":{"name":{"type":"string","description":"The user's name"}},"required":["name"]}}},
              {"type":"function","function":{
                "name":"run_simulation",
                "description":"Load the family demo data: Anna, Péter, Eszter, Bence",
                "parameters":{"type":"object","properties":{}}}},
              {"type":"function","function":{
                "name":"open_app",
                "description":"Open an application for a specific user",
                "parameters":{"type":"object","properties":{
                  "user_name":{"type":"string","description":"The user's name"},
                  "app_type":{"type":"string","enum":["MINESWEEPER","OPENMAP","PAINT","CONTACTS"]}},
                  "required":["user_name","app_type"]}}},
              {"type":"function","function":{
                "name":"add_app_to_menu",
                "description":"Add an application shortcut to a user's main menu",
                "parameters":{"type":"object","properties":{
                  "user_name":{"type":"string","description":"The user's name"},
                  "app_type":{"type":"string","enum":["MINESWEEPER","OPENMAP","PAINT","CONTACTS"]}},
                  "required":["user_name","app_type"]}}}
            ]
            """;

    public record ToolCall(String name, Map<String, Object> arguments) {}

    private static final String SYSTEM_PROMPT =
            "You are a SmartOS assistant. Always respond by calling one of the provided tools. Never reply with plain text.";

    public Optional<ToolCall> call(String userMessage) {
        try {
            String body = mapper.writeValueAsString(Map.of(
                    "model", MODEL,
                    "messages", new Object[]{
                            Map.of("role", "system", "content", SYSTEM_PROMPT),
                            Map.of("role", "user",   "content", userMessage)
                    },
                    "tools", mapper.readTree(TOOLS),
                    "tool_choice", "required"
            ));

            String response = RestClient.create()
                    .post()
                    .uri(URL)
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .body(body)
                    .retrieve()
                    .body(String.class);

            JsonNode root      = mapper.readTree(response);
            JsonNode toolCalls = root.path("choices").get(0).path("message").path("tool_calls");

            if (toolCalls.isMissingNode() || toolCalls.isEmpty()) {
                String text = root.path("choices").get(0).path("message").path("content").asText("");
                if (!text.isBlank()) System.out.println(text);
                return Optional.empty();
            }

            JsonNode fn   = toolCalls.get(0).path("function");
            String   name = fn.path("name").asText();
            @SuppressWarnings("unchecked")
            Map<String, Object> args = mapper.readValue(fn.path("arguments").asText(), Map.class);
            return Optional.of(new ToolCall(name, args));

        } catch (Exception e) {
            System.out.println("LLM error: " + e.getMessage());
            return Optional.empty();
        }
    }
}
