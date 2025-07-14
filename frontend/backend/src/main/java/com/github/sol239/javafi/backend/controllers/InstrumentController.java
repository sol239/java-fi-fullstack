package com.github.sol239.javafi.backend.controllers;

import com.github.sol239.javafi.backend.services.InstrumentService;
import com.github.sol239.javafi.backend.services.TableService;
import com.github.sol239.javafi.backend.utils.instrument.InstrumentExecutor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/instrument")
public class InstrumentController {

    private final InstrumentService instrumentService;

    public InstrumentController(InstrumentService instrumentService) {
        this.instrumentService = instrumentService;
    }


    @PostMapping("run")
    public void runInstrument(@RequestParam String instrumentConsoleString,
                              @RequestParam String tableName) {
        System.out.println("Running instrument: " + instrumentConsoleString + " on table: " + tableName);
        instrumentService.runInstrument(instrumentConsoleString, tableName);
        System.out.println("Finished running instrument: " + instrumentConsoleString + " on table: " + tableName);
    }

}
