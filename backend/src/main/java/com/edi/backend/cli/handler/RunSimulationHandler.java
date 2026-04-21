package com.edi.backend.cli.handler;

import com.edi.backend.service.SimulationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Map;

@Profile("dev")
@Component
@RequiredArgsConstructor
public class RunSimulationHandler implements ToolHandler {

    private final SimulationService simulationService;

    @Override
    public String name() {
        return "run_simulation";
    }

    @Override
    public String toolDefinition() {
        return """
                {"type":"function","function":{
                  "name":"run_simulation",
                  "description":"Load the family demo data: Anna, Péter, Eszter, Bence",
                  "parameters":{"type":"object","properties":{}}}}""";
    }

    @Override
    public void execute(Map<String, Object> args) {
        simulationService.run();
        System.out.println("Demo data loaded: Anna, Péter, Eszter, Bence");
    }
}
