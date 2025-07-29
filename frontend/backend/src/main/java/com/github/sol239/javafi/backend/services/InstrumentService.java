package com.github.sol239.javafi.backend.services;

import com.github.sol239.javafi.backend.utils.instrument.InstrumentExecutor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Service class for managing instruments.
 * Provides methods to run instruments and retrieve the count of instruments.
 */
@Service
public class InstrumentService {

    /**
     * Executor for running instruments.
     */
    private final InstrumentExecutor instrumentExecutor;

    /**
     * Constructor for InstrumentService.
     * @param instrumentExecutor the executor to run instruments
     */
    public InstrumentService(InstrumentExecutor instrumentExecutor) {
        this.instrumentExecutor = instrumentExecutor;
    }

    /**
     * Runs an instrument based on the provided console string and table name.
     * The console string should be in the format "instrumentName:arg1,arg2,...".
     *
     * @param instrumentConsoleString the instrument command string
     * @param tableName the name of the table on which to run the instrument
     */
    public void runInstrument(String instrumentConsoleString, String tableName) {
        String[] parts = instrumentConsoleString.split(":");
        String[] args = parts[1].split(",");
        Double[] doubleArgs = new Double[args.length];
        for (int i = 0; i < args.length; i++) {
            doubleArgs[i] = Double.parseDouble(args[i]);
        }
        String instrumentName = parts[0].trim();
        Map<String, Double[]> instrumentArgs = new HashMap<>();
        instrumentArgs.put(instrumentName, doubleArgs);
        InstrumentExecutor.runInstrument(tableName, instrumentArgs);
    }

    /**
     * Returns the count of instruments currently managed by the executor.
     * @return the number of instruments
     */
    public int getInstrumentCount() {
        return instrumentExecutor.getInstrumentCount();
    }

}
