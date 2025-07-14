package com.github.sol239.javafi.backend.services;

import com.github.sol239.javafi.backend.utils.instrument.InstrumentExecutor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class InstrumentService {
    private final InstrumentExecutor instrumentExecutor;

    public InstrumentService(InstrumentExecutor instrumentExecutor) {
        this.instrumentExecutor = instrumentExecutor;
    }

    public void runInstrument(String instrumentConsoleString, String tableName) {
        // instrumentConsoleString is in format "instrumentName:arg1,arg2,..."
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

    public int getInstrumentCount() {
        return instrumentExecutor.getInstrumentCount();
    }

}
