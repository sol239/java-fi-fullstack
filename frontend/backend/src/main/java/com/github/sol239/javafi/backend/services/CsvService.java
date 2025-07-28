package com.github.sol239.javafi.backend.services;

import com.github.sol239.javafi.backend.repositories.RowRepository;
import com.github.sol239.javafi.backend.utils.database.DBHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

@Service
public class CsvService {

    private final RowRepository rowRepository;
    private final DBHandler dbHandler;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public CsvService(RowRepository rowRepository, DBHandler dbHandler) {
        this.rowRepository = rowRepository;
        this.dbHandler = dbHandler;
    }

    public String getLastId(String tableName) {
        return dbHandler.getLastId(tableName);
    }

    public void insertCsvData(String tableName, InputStream csvInputStream) {
        dbHandler.insertCsvData(tableName, csvInputStream);
    }

    public void createIndex(String tableName, String columnName) {
        dbHandler.createIndex(tableName, columnName);
    }

    public ResultSet getLastNRows(String tableName, int numberOfRows) {
        return dbHandler.getLastNRows(tableName, numberOfRows);
    }

    public List<Map<String, Object>> getRowsFromTo(String tableName, int numberOfRows, String fromId, String toId) {
        return getDataFromTo(tableName, numberOfRows, fromId, toId);
    }

    public List<Map<String, Object>> getDataFromTo(String tableName, long count, String from, String to) {
        String query = "SELECT * FROM " + tableName + " WHERE id >= ? AND id <= ? LIMIT ?";
        return jdbcTemplate.queryForList(query, from, to, count);
    }
}
