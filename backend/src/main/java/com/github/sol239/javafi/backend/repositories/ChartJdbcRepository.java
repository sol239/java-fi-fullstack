package com.github.sol239.javafi.backend.repositories;

import com.github.sol239.javafi.backend.entity.Chart;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.ArrayList;

/**
 * Repository for managing chart metadata in the database.
 * Provides methods to find all charts, find charts from a specific table,
 * and save new charts to a specified table.
 */
@Repository
public class ChartJdbcRepository {

    /**
     * JdbcTemplate for executing SQL queries.
     */
    private final JdbcTemplate jdbcTemplate;

    /**
     * Constructor for ChartJdbcRepository.
     * @param jdbcTemplate JdbcTemplate to be used for database operations
     */
    public ChartJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Finds all charts in the database.
     * @return a list of all Chart objects found in the database
     */
    public List<Chart> findAll() {
        String tablesSql = "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME LIKE '%\\_META' ESCAPE '\\'";
        List<String> metaTables = jdbcTemplate.queryForList(tablesSql, String.class);

        List<Chart> allCharts = new ArrayList<>();
        for (String table : metaTables) {
            String sql = "SELECT id, name, description, asset_name, timeframe FROM " + table;
            allCharts.addAll(jdbcTemplate.query(sql, chartRowMapper));
        }
        return allCharts;
    }

    /**
     * Saves a new chart to the database.
     * @param chart the Chart object to be saved
     * @param tableName the name of the table where the chart metadata will be saved
     * @return the number of rows affected by the insert operation
     */
    public int save(Chart chart, String tableName) {
        String metaTable = tableName + "_META";

        createMetaTableIfNotExists(metaTable);

        String insertSql = "INSERT INTO " + metaTable + " (name, description, asset_name, timeframe) VALUES (?, ?, ?, ?)";
        return jdbcTemplate.update(insertSql,
                chart.getName(),
                chart.getDescription(),
                chart.getAssetName(),
                chart.getTimeframe());
    }

    /**
     * Creates a metadata table if it does not already exist.
     * @param tableName the name of the table to be created
     */
    private void createMetaTableIfNotExists(String tableName) {
        String createTableSql = "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "name VARCHAR(255), " +
                "description VARCHAR(255), " +
                "asset_name VARCHAR(255), " +
                "timeframe VARCHAR(50)" +
                ")";
        jdbcTemplate.execute(createTableSql);
    }

    /**
     * RowMapper for mapping SQL ResultSet to Chart objects.
     */
    private final RowMapper<Chart> chartRowMapper = (rs, rowNum) -> {
        Chart chart = new Chart();
        chart.setId(rs.getLong("id"));
        chart.setName(rs.getString("name"));
        chart.setDescription(rs.getString("description"));
        chart.setAssetName(rs.getString("asset_name"));
        chart.setTimeframe(rs.getString("timeframe"));
        return chart;
    };
}
