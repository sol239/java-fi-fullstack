package com.github.sol239.javafi.backend.controllers;

import com.github.sol239.javafi.backend.services.InstrumentService;
import com.github.sol239.javafi.backend.services.TableService;
import com.github.sol239.javafi.backend.utils.instrument.InstrumentExecutor;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for handling instrument-related requests.
 * This controller provides an endpoint to run instruments on a specified table.
 */
@RestController
@RequestMapping("/api/instrument")
public class InstrumentController {

    /**
     * Logger for logging information and errors in the InstrumentController.
     */
    private static final Logger logger = LoggerFactory.getLogger(InstrumentController.class);

    /**
     * Service for handling instrument operations.
     */
    private final InstrumentService instrumentService;

    /**
     * Constructor for InstrumentController.
     * @param instrumentService the service to handle instrument operations
     */
    public InstrumentController(InstrumentService instrumentService) {
        this.instrumentService = instrumentService;
    }

    /**
     * Endpoint to run an instrument on a specified table.
     * @param instrumentConsoleString the instrument command string in the format "instrument:arg1,arg2,..."
     * @param tableName the name of the table on which to run the instrument
     */
    @PostMapping("run")
    public void runInstrument(@RequestParam String instrumentConsoleString,
                              @RequestParam String tableName) {
        logger.info("POST:api/instrument/run InstrumentController.runInstrument() called for instrument: {} on table: {}", instrumentConsoleString, tableName);
        instrumentService.runInstrument(instrumentConsoleString, tableName);
        logger.info("Finished running instrument: {} on table: {}", instrumentConsoleString, tableName);
    }

    /**
     * Endpoint to retrieve the names of all available instruments and their usage.
     * @return a map of instrument names and their example usage
     */
    @GetMapping
    public Map<String, String > getAvailableInstruments() {
        logger.info("GET:api/instrument InstrumentController.getAvailableInstruments() called");
        List<String> instruments = instrumentService.getAvailableInstruments();

        Map<String, String> map = new HashMap<>();

        for (String instrument : instruments) {
            map.put(instrument, instrumentService.getExampleUsageOfInstrument(instrument));
        }

        return map;
    }

}
