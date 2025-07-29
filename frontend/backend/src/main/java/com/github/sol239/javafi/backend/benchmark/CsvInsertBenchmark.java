package com.github.sol239.javafi.backend.benchmark;


import com.github.sol239.javafi.backend.utils.database.DBHandler;
import org.openjdk.jmh.annotations.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

/**
 * Benchmark for inserting CSV data into a database table.
 */
@BenchmarkMode(Mode.SingleShotTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
@Fork(value = 1, warmups = 0)
@Warmup(iterations = 3)
@Measurement(iterations = 3)
public class CsvInsertBenchmark {

    /**
     * Inserts CSV data into a database table and measures the time taken.
     * The table is created, indexed, and then deleted after the operation.
     */
    @Benchmark
    public void insertCsvData() {
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
        handler.deleteTable(tableName);
    }
}
