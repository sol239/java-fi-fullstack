package com.github.sol239.javafi.backend.benchmark;

import com.github.sol239.javafi.backend.services.CsvService;
import com.github.sol239.javafi.backend.services.InstrumentService;
import com.github.sol239.javafi.backend.utils.database.DBHandler;
import com.github.sol239.javafi.backend.utils.instrument.InstrumentExecutor;
import org.openjdk.jmh.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Benchmark for the InstrumentExecutor class.
 */
@BenchmarkMode(Mode.SingleShotTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
@Fork(value = 1, warmups = 0)
@Warmup(iterations = 3)
@Measurement(iterations = 3)
public class InstrumentExecutorBenchmark {


    /**
     * This benchmark tests the performance of creating a table, inserting data from a CSV file and calculating
     * the RSI (Relative Strength Index) using the InstrumentExecutor class.
     */
    @Benchmark
    public void createTableAndCalculateRsi() {

        String tableName = "BENCHMARK_TABLE";
        DBHandler handler = new DBHandler("jdbc:h2:file:./data/mydb");

        InputStream is;
        try {
            is = new FileInputStream("assets/csv/BTCUSD_1D.csv");

        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + e.getMessage());
            return;
        }

        handler.insertCsvData(tableName, is);
        handler.createIndex(tableName, "id");
        handler.createIndex(tableName, "DATE");

        String instrumentConsoleString = "rsi:14";
        String[] parts = instrumentConsoleString.split(":");
        String[] args = parts[1].split(",");
        Double[] doubleArgs = new Double[args.length];
        for (int i = 0; i < args.length; i++) {
            doubleArgs[i] = Double.parseDouble(args[i]);
        }
        String instrumentName = parts[0].trim();
        Map<String, Double[]> instrumentArgs = new HashMap<>();
        instrumentArgs.put(instrumentName, doubleArgs);

        InstrumentExecutor instrumentExecutor = new InstrumentExecutor(handler);
        instrumentExecutor.runInstrument(tableName, instrumentArgs);

        handler.deleteTable(tableName);

    }


}
