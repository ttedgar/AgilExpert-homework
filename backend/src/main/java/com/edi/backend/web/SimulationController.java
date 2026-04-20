package com.edi.backend.web;

import com.edi.backend.service.SimulationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class SimulationController {

    private final SimulationService simulationService;

    @PostMapping("/simulation")
    public String run() {
        simulationService.run();
        return "redirect:/users";
    }
}
