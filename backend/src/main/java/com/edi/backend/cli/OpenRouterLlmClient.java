package com.edi.backend.cli;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;
import java.util.Optional;

@Component
public class OpenRouterLlmClient implements LlmClient {

    @Value("${openrouter.api-key}")
    private String apiKey;

    @Value("${openrouter.url}")
    private String url;

    @Value("${openrouter.model}")
    private String model;

    @Value("${openrouter.system-prompt}")
    private String systemPrompt;

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public Optional<ToolCall> call(String userMessage, String toolsJson) {
        try {
            String body = mapper.writeValueAsString(Map.of(
                    "model", model,
                    "messages", new Object[]{
                            Map.of("role", "system", "content", systemPrompt),
                            Map.of("role", "user",   "content", userMessage)
                    },
                    "tools", mapper.readTree(toolsJson),
                    "tool_choice", "required"
            ));

            String response = RestClient.create()
                    .post()
                    .uri(url)
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
