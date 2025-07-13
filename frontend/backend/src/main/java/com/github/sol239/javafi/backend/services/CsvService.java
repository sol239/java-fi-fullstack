package com.github.sol239.javafi.backend.services;

import com.github.sol239.javafi.backend.repositories.ChartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.*;

@Service
public class CsvService {

    private final ChartRepository chartRepository;
    private final DataSource dataSource;

    @Autowired
    public CsvService(ChartRepository chartRepository, DataSource dataSource) {
        this.chartRepository = chartRepository;
        this.dataSource = dataSource;
    }

    public void insertCsvData(String tableName, InputStream csvInputStream) {
        try (Connection conn = dataSource.getConnection();
             BufferedReader br = new BufferedReader(new InputStreamReader(csvInputStream))) {
            String headerLine = br.readLine();
            if (headerLine == null) {
                System.out.println("FAIL - Empty CSV file.");
                return;
            }

            String[] csvHeaders = headerLine.toLowerCase().split(",");
            List<String> dbColumns = Arrays.asList("timestamp", "open", "high", "low", "close", "volume", "date");

            String createTableSQL = "CREATE TABLE IF NOT EXISTS " + tableName + " ("
                    + "timestamp BIGINT,"
                    + "open DOUBLE,"
                    + "high DOUBLE,"
                    + "low DOUBLE,"
                    + "close DOUBLE,"
                    + "volume DOUBLE,"
                    + "date TIMESTAMP"
                    + ");";
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(createTableSQL);
            }

            Map<String, Integer> columnIndexMap = new HashMap<>();
            for (int i = 0; i < csvHeaders.length; i++) {
                if (dbColumns.contains(csvHeaders[i])) {
                    columnIndexMap.put(csvHeaders[i], i);
                }
            }

            List<String> matchedColumns = new ArrayList<>(columnIndexMap.keySet());
            if (matchedColumns.isEmpty()) {
                System.out.println("FAIL - No matching columns found.");
                return;
            }

            String sql = "INSERT INTO " + tableName + " (" + String.join(",", matchedColumns) + ") VALUES (" +
                    String.join(",", Collections.nCopies(matchedColumns.size(), "?")) + ")";

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] values = line.split(",");
                    int paramIndex = 1;
                    for (String col : matchedColumns) {
                        int csvIndex = columnIndexMap.get(col);
                        String value = csvIndex < values.length ? values[csvIndex] : null;

                        if (value == null || value.isEmpty()) {
                            pstmt.setNull(paramIndex, Types.NULL);
                        } else if (col.equals("timestamp")) {
                            pstmt.setLong(paramIndex, Long.parseLong(value));
                        } else if (Arrays.asList("open", "high", "low", "close", "volume").contains(col)) {
                            pstmt.setDouble(paramIndex, Double.parseDouble(value));
                        } else if (col.equals("date")) {
                            pstmt.setTimestamp(paramIndex, Timestamp.valueOf(value));
                        }
                        paramIndex++;
                    }
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }

            System.out.println("SUCCESS - InsertCsvData: " + tableName);
        } catch (SQLException | IOException e) {
            System.out.println("FAIL - InsertCsvData: " + e.getMessage());
        }
    }
}
