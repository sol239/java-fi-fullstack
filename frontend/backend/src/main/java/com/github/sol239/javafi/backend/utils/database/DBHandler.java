package com.github.sol239.javafi.backend.utils.database;

import com.github.sol239.javafi.backend.controllers.ChartDataController;
import com.github.sol239.javafi.backend.utils.DataObject;
import com.github.sol239.javafi.backend.utils.instrument.IdValueRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

@Component
public class DBHandler {

    private final DataSource dataSource;

    @Autowired
    public DBHandler(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static final List<String> SYSTEM_TABLES = Arrays.asList(
            "CONSTANTS",
            "ENUM_VALUES",
            "INDEXES",
            "INDEX_COLUMNS",
            "INFORMATION_SCHEMA_CATALOG_NAME",
            "IN_DOUBT",
            "LOCKS",
            "QUERY_STATISTICS",
            "RIGHTS",
            "ROLES",
            "SESSIONS",
            "SESSION_STATE",
            "SETTINGS",
            "SYNONYMS",
            "USERS"
    );

    public boolean isConnected() {
        try (Connection conn = dataSource.getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    public List<String> getAllTables() {
        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData dbmd = conn.getMetaData();
            ResultSet rs = dbmd.getTables(null, null, "%", new String[]{"TABLE"});
            List<String> tables = new ArrayList<>();
            while (rs.next()) {
                String tableName = rs.getString(3);
                if (!SYSTEM_TABLES.contains(tableName)) {
                    tables.add(tableName);
                }
            }
            return tables;
        } catch (SQLException e) {
            System.out.println("Error getting tables: " + e.getMessage());
            return null;
        }
    }

    public void clean(String tableName) {
        String sql = "SELECT column_name FROM INFORMATION_SCHEMA.COLUMNS WHERE table_name = '" + tableName.toUpperCase() + "';";
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            List<String> columns = new ArrayList<>();
            while (rs.next()) {
                columns.add(rs.getString(1));
            }
            columns.remove("TIMESTAMP");
            columns.remove("OPEN");
            columns.remove("HIGH");
            columns.remove("LOW");
            columns.remove("CLOSE");
            columns.remove("VOLUME");
            columns.remove("DATE");
            columns.remove("ID");
            for (String column : columns) {
                String dropSql = "ALTER TABLE " + tableName + " DROP COLUMN \"" + column + "\";";
                stmt.execute(dropSql);
            }
            System.out.println("Table cleaned successfully.");
        } catch (SQLException e) {
            System.out.println("Error cleaning table: " + e.getMessage());
        }
    }

    public void deleteTable(String tableName) {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            String sql = "DROP TABLE " + tableName + ";";
            stmt.execute(sql);
            System.out.println("Table deleted successfully.");
        } catch (SQLException e) {
            System.out.println("Error deleting table: " + e.getMessage());
        }
    }

    // DEPRECATED: Use getResultSetAsList instead to avoid connection leaks
    @Deprecated
    public ResultSet getResultSet(String query) {
        System.err.println("WARNING: getResultSet is deprecated and causes connection leaks. Use getResultSetAsList instead.");
        try {
            Connection conn = dataSource.getConnection();
            return conn.createStatement().executeQuery(query);
        } catch (SQLException e) {
            System.out.println("Error creating cached result set: " + e.getMessage());
            return null;
        }
    }

    public String getLastId(String tableName) {
        String query = "SELECT COUNT(id) AS row_count FROM " + tableName + ";";
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return String.valueOf(rs.getLong("row_count"));
            }
        } catch (SQLException e) {
            System.out.println("Error getting row count: " + e.getMessage());
        }
        return null;
    }

    public List<ChartDataController.ChartDTO> getDataFromToAsList(String tableName, String from, String to) {
        String query = "SELECT * FROM " + tableName + " WHERE date >= '" + from + "' AND date <= '" + to + "';";
        List<ChartDataController.ChartDTO> result = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                long id = rs.getLong("id");
                long timestamp = rs.getLong("timestamp") / 1000;
                double open = rs.getDouble("open");
                double high = rs.getDouble("high");
                double low = rs.getDouble("low");
                double close = rs.getDouble("close");
                double volume = rs.getDouble("volume");
                LocalDateTime date = rs.getObject("date", LocalDateTime.class);
                result.add(new ChartDataController.ChartDTO(id, timestamp, open, high, low, close, volume, date));
            }
        } catch (SQLException e) {
            System.out.println("Error executing query: " + e.getMessage());
        }
        return result;
    }

    // DEPRECATED: Use getDataAfterAsList instead
    @Deprecated
    public ResultSet getDataAfter(String tableName, String afterDate, int numberOfRows) {
        String query = "SELECT * FROM " + tableName + " WHERE date >= '" + afterDate + "' LIMIT " + numberOfRows + ";";
        try {
            Connection conn = dataSource.getConnection();
            return conn.createStatement().executeQuery(query);
        } catch (SQLException e) {
            System.out.println("Error creating cached result set: " + e.getMessage());
            return null;
        }
    }

    public List<ChartDataController.ChartDTO> getDataAfterAsList(String tableName, String afterDate, int numberOfRows) {
        String query = "SELECT * FROM " + tableName + " WHERE date >= '" + afterDate + "' LIMIT " + numberOfRows + ";";
        List<ChartDataController.ChartDTO> result = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                long id = rs.getLong("id");
                long timestamp = rs.getLong("timestamp") / 1000;
                double open = rs.getDouble("open");
                double high = rs.getDouble("high");
                double low = rs.getDouble("low");
                double close = rs.getDouble("close");
                double volume = rs.getDouble("volume");
                LocalDateTime date = rs.getObject("date", LocalDateTime.class);
                result.add(new ChartDataController.ChartDTO(id, timestamp, open, high, low, close, volume, date));
            }
        } catch (SQLException e) {
            System.out.println("Error executing query: " + e.getMessage());
        }
        return result;
    }


    public List<ChartDataController.ChartDTO> getDataBeforeAsList(String tableName, String beforeDate, int numberOfRows) {
        String query = "SELECT * FROM " + tableName + " WHERE date <= '" + beforeDate + "' ORDER BY date DESC LIMIT " + numberOfRows + ";";
        List<ChartDataController.ChartDTO> result = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                long id = rs.getLong("id");
                long timestamp = rs.getLong("timestamp") / 1000;
                double open = rs.getDouble("open");
                double high = rs.getDouble("high");
                double low = rs.getDouble("low");
                double close = rs.getDouble("close");
                double volume = rs.getDouble("volume");
                LocalDateTime date = rs.getObject("date", LocalDateTime.class);
                result.add(new ChartDataController.ChartDTO(id, timestamp, open, high, low, close, volume, date));
            }
        } catch (SQLException e) {
            System.out.println("Error executing query: " + e.getMessage());
        }
        return result;
    }

    public List<ChartDataController.ChartDTO> getFirstNRowsAsList(String tableName, int numberOfRows) {
        String query = "SELECT * FROM " + tableName + " ORDER BY TIMESTAMP ASC LIMIT " + numberOfRows + ";";
        List<ChartDataController.ChartDTO> result = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                long id = rs.getLong("id");
                long timestamp = rs.getLong("timestamp") / 1000;
                double open = rs.getDouble("open");
                double high = rs.getDouble("high");
                double low = rs.getDouble("low");
                double close = rs.getDouble("close");
                double volume = rs.getDouble("volume");
                LocalDateTime date = rs.getObject("date", LocalDateTime.class);
                result.add(new ChartDataController.ChartDTO(id, timestamp, open, high, low, close, volume, date));
            }
        } catch (SQLException e) {
            System.out.println("Error executing query: " + e.getMessage());
        }
        return result;
    }

    // DEPRECATED: Use getLastNRowsAsList instead
    @Deprecated
    public ResultSet getLastNRows(String tableName, int numberOfRows) {
        System.err.println("WARNING: getLastNRows is deprecated and causes connection leaks. Use getLastNRowsAsList instead.");
        String query = "SELECT * FROM " + tableName + " ORDER BY TIMESTAMP DESC LIMIT " + numberOfRows + ";";
        try {
            Connection conn = dataSource.getConnection();
            return conn.createStatement().executeQuery(query);
        } catch (SQLException e) {
            System.out.println("Error executing query: " + e.getMessage());
            return null;
        }
    }

    public List<ChartDataController.ChartDTO> getLastNRowsAsList(String tableName, int numberOfRows) {
        String query = "SELECT * FROM " + tableName + " ORDER BY TIMESTAMP DESC LIMIT " + numberOfRows + ";";
        List<ChartDataController.ChartDTO> result = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                long id = rs.getLong("id");
                long timestamp = rs.getLong("timestamp") / 1000;
                double open = rs.getDouble("open");
                double high = rs.getDouble("high");
                double low = rs.getDouble("low");
                double close = rs.getDouble("close");
                double volume = rs.getDouble("volume");
                LocalDateTime date = rs.getObject("date", LocalDateTime.class);
                result.add(new ChartDataController.ChartDTO(id, timestamp, open, high, low, close, volume, date));
            }
        } catch (SQLException e) {
            System.out.println("Error executing query: " + e.getMessage());
        }
        return result;
    }

    public void setFetchSize(int size) {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.setFetchSize(size);
        } catch (SQLException e) {
            System.out.println("Error setting fetch size: " + e.getMessage());
        }
    }

    public void closeConnection() {
        // Není potřeba, Spring spravuje DataSource a připojení se zavírají automaticky v try-with-resources.
    }

    public void executeQuery(String query) {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates an index on the specified column of the specified table.
     * @param tableName the name of the table
     * @param columnName the name of the column to index
     */
    public void createIndex(String tableName, String columnName) {
        String sql = "CREATE INDEX IF NOT EXISTS idx_" + columnName + " ON " + tableName + " (" + columnName + ");";
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Index created successfully.");
        } catch (SQLException e) {
            System.out.println("Error creating index: " + e.getMessage());
        }
    }

    /**
     * Inserts data from a CSV file into the specified table.
     * @param tableName the name of the table to insert data into
     * @param csvInputStream the InputStream of the CSV file
     */
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
                    + "id SERIAL PRIMARY KEY,"
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

    public DataObject insertColumns(String tableName, HashMap<String, List<IdValueRecord>> columnMap) {
        try (Connection conn = dataSource.getConnection()) {
            for (String columnName : columnMap.keySet()) {
                String createColumnQuery = """
                        ALTER TABLE IF EXISTS %s
                        ADD COLUMN IF NOT EXISTS %s DOUBLE DEFAULT 0;
                        """.formatted(tableName, columnName);
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute(createColumnQuery);
                }
            }
            int size = columnMap.values().stream().findFirst().get().size();
            for (int i = 0; i < size; i++) {
                StringBuilder updateSb = new StringBuilder();
                updateSb.append("UPDATE ").append(tableName);
                updateSb.append("\n");
                updateSb.append("SET ");
                for (String columnName : columnMap.keySet()) {
                    updateSb.append(columnName).append(" = ").append(columnMap.get(columnName).get(i).value()).append(", ").append("\n");
                }
                updateSb.deleteCharAt(updateSb.length() - 3);
                updateSb.append("WHERE id = ").append(i + 1).append(";\n");
                String updateSQL = updateSb.toString();
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute(updateSQL);
                }
            }
            return new DataObject(200, "server", "Columns created and updated successfully with batch processing.");
        } catch (Exception e) {
            System.out.println("Error in batch insertColumns: " + e.getMessage());
            e.printStackTrace();
            return new DataObject(500, "server", "Error creating and updating columns: " + e.getMessage());
        }
    }

    public DataObject insertColumnsBatch(String tableName, HashMap<String, List<IdValueRecord>> columnMap) {
        try (Connection conn = dataSource.getConnection()) {
            boolean originalAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            final int BATCH_SIZE = 5000;
            for (String columnName : columnMap.keySet()) {
                String createColumnQuery = """
                    ALTER TABLE IF EXISTS %s
                    ADD COLUMN IF NOT EXISTS %s DOUBLE DEFAULT 0;
                    """.formatted(tableName, columnName);
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute(createColumnQuery);
                }
            }
            int size = columnMap.isEmpty() ? 0 : columnMap.values().iterator().next().size();
            if (size > 0) {
                StringBuilder sqlBuilder = new StringBuilder();
                sqlBuilder.append("UPDATE ").append(tableName).append(" SET ");
                List<String> columnNames = new ArrayList<>(columnMap.keySet());
                for (int i = 0; i < columnNames.size(); i++) {
                    sqlBuilder.append(columnNames.get(i)).append(" = ?");
                    if (i < columnNames.size() - 1) {
                        sqlBuilder.append(", ");
                    }
                }
                sqlBuilder.append(" WHERE id = ?");
                String updateSQL = sqlBuilder.toString();
                try (PreparedStatement pstmt = conn.prepareStatement(updateSQL)) {
                    int batchCount = 0;
                    for (int i = 0; i < size; i++) {
                        for (int colIndex = 0; colIndex < columnNames.size(); colIndex++) {
                            String columnName = columnNames.get(colIndex);
                            double value = columnMap.get(columnName).get(i).value();
                            pstmt.setDouble(colIndex + 1, value);
                        }
                        pstmt.setInt(columnNames.size() + 1, i + 1);
                        pstmt.addBatch();
                        batchCount++;
                        if (batchCount >= BATCH_SIZE || i == size - 1) {
                            pstmt.executeBatch();
                            conn.commit();
                            batchCount = 0;
                        }
                    }
                }
            }
            conn.commit();
            conn.setAutoCommit(originalAutoCommit);
            return new DataObject(200, "server", "Columns created and updated successfully with batch processing.");
        } catch (Exception e) {
            System.out.println("Error in batch insertColumns: " + e.getMessage());
            e.printStackTrace();
            return new DataObject(500, "server", "Error creating and updating columns: " + e.getMessage());
        }
    }
}

