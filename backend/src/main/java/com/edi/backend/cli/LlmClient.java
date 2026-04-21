package com.edi.backend.cli;

import java.util.Map;
import java.util.Optional;

public interface LlmClient {

    Optional<ToolCall> call(String userMessage, String toolsJson);

    record ToolCall(String name, Map<String, Object> arguments) {}
}
