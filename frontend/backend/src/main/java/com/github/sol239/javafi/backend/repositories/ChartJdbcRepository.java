package com.github.sol239.javafi.backend.repositories;

import com.github.sol239.javafi.backend.entity.Chart;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.ArrayList;

@Repository
public class ChartJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public ChartJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

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

    public List<Chart> findAllFromTable(String tableName) {
        String sql = "SELECT id, name, description, asset_name, timeframe FROM " + tableName;
        return jdbcTemplate.query(sql, chartRowMapper);
    }

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
