package com.github.sol239.javafi.backend.repositories;

import com.github.sol239.javafi.backend.entity.Chart;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public class ChartJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    // Optionally: maintain a whitelist of allowed table names for safety
    private static final Set<String> ALLOWED_TABLES = Set.of("charts", "custom_chart_1", "custom_chart_2");

    public ChartJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Chart> findAll() {
        String sql = "SELECT id, name, description, asset_name, timeframe FROM charts";
        return jdbcTemplate.query(sql, chartRowMapper);
    }

    public List<Chart> findAllFromTable(String tableName) {
        validateTableName(tableName);
        String sql = "SELECT id, name, description, asset_name, timeframe FROM " + tableName;
        return jdbcTemplate.query(sql, chartRowMapper);
    }

    public int save(Chart chart, String tableName) {
        String metaTable = tableName + "_META";
        validateTableName(metaTable);

        createMetaTableIfNotExists(metaTable);

        String insertSql = "INSERT INTO " + metaTable + " (name, description, asset_name, timeframe) VALUES (?, ?, ?, ?)";
        return jdbcTemplate.update(insertSql,
                chart.getName(),
                chart.getDescription(),
                chart.getAssetName(),
                chart.getTimeframe());
    }

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

    private void validateTableName(String tableName) {
        if (!ALLOWED_TABLES.contains(tableName.toLowerCase())) {
            throw new IllegalArgumentException("Invalid or unauthorized table name: " + tableName);
        }
    }

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
