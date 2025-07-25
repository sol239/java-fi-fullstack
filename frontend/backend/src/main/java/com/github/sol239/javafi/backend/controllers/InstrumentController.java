package com.github.sol239.javafi.backend.controllers;

import com.github.sol239.javafi.backend.services.InstrumentService;
import com.github.sol239.javafi.backend.services.TableService;
import com.github.sol239.javafi.backend.utils.instrument.InstrumentExecutor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/instrument")
public class InstrumentController {
    private static final Logger logger = LoggerFactory.getLogger(InstrumentController.class);

    private final InstrumentService instrumentService;

    public InstrumentController(InstrumentService instrumentService) {
        this.instrumentService = instrumentService;
    }


    @PostMapping("run")
    public void runInstrument(@RequestParam String instrumentConsoleString,
                              @RequestParam String tableName) {
        logger.info("POST:api/instrument/run InstrumentController.runInstrument() called for instrument: {} on table: {}", instrumentConsoleString, tableName);
        instrumentService.runInstrument(instrumentConsoleString, tableName);
        logger.info("Finished running instrument: {} on table: {}", instrumentConsoleString, tableName);
    }

}
