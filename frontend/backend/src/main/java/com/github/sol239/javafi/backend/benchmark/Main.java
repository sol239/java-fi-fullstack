package com.github.sol239.javafi.backend.benchmark;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class Main {
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
