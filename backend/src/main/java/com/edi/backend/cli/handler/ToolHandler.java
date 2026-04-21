package com.edi.backend.cli.handler;

import java.util.Map;

public interface ToolHandler {

    String name();

    String toolDefinition();

    void execute(Map<String, Object> args);
}
