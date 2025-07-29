package com.github.sol239.javafi.backend.benchmark;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * Main class to run JMH benchmarks.
 */
public class Main {

    /**
     * Main method to execute the JMH benchmarks. It saves benchmarks in BENCHMARK_RESULT_PATH stored in .env file.
     * @param args Command line arguments (not used)
     * @throws Exception if an error occurs while reading the properties file or running the benchmarks
     */
    public static void main(String[] args) throws Exception {
        Properties env = new Properties();
        try (InputStream input = new FileInputStream(".env")) {
            env.load(input);
        }

        String outputDir = env.getProperty("BENCHMARK_RESULT_PATH", "./");
        if (!outputDir.endsWith("/") && !outputDir.endsWith("\\")) {
            outputDir += File.separator;
        }

        String timestamp = new SimpleDateFormat("MM-dd-yyyy-HH-mm-ss").format(new Date());
        String filename = outputDir + "benchmark-results-" + timestamp + ".json";

        String[] jmhArgs = {
                "-rf", "json",
                "-rff", filename
        };

        org.openjdk.jmh.Main.main(jmhArgs);
    }
}
