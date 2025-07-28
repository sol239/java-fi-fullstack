package com.github.sol239.javafi.backend.benchmark;


import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
@Fork(value = 1, warmups = 0)
@Warmup(iterations = 3)
@Measurement(iterations = 3)
public class CsvInsertBenchmark {

    @Benchmark
    public void insertCsvData() {
        // TODO: Implement the CSV insertion logic here
    }
}
