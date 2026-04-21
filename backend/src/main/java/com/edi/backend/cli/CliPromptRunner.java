package com.edi.backend.cli;

import com.edi.backend.cli.handler.ToolHandler;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Function;
import java.util.stream.Collectors;

@Profile("dev")
@Component
@Order(2)
@RequiredArgsConstructor
public class CliPromptRunner implements ApplicationRunner {

    private final LlmClient llmClient;
    private final List<ToolHandler> toolHandlers;

    private Map<String, ToolHandler> handlerMap;
    private String toolsJson;

    @PostConstruct
    private void init() {
        handlerMap = toolHandlers.stream()
                .collect(Collectors.toMap(ToolHandler::name, Function.identity()));
        toolsJson = toolHandlers.stream()
                .map(ToolHandler::toolDefinition)
                .collect(Collectors.joining(",", "[", "]"));
    }

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
            llmClient.call(input, toolsJson).ifPresentOrElse(
                    this::dispatch,
                    () -> System.out.println("I didn't understand that. Try something like: 'open the map for Péter'")
            );
        }
    }

    private void dispatch(LlmClient.ToolCall toolCall) {
        try {
            ToolHandler handler = handlerMap.get(toolCall.name());
            if (handler == null) {
                System.out.println("Unknown tool: " + toolCall.name());
                return;
            }
            handler.execute(toolCall.arguments());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
