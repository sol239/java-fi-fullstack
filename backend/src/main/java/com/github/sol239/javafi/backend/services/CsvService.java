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

/**
 * Service for handling CSV file operations and database interactions.
 * Provides methods to insert CSV data, create indexes, and retrieve rows from the database.
 */
@Service
public class CsvService {

    /**
     * Repository for managing Row entities.
     */
    private final RowRepository rowRepository;

    /**
     * Handler for database operations.
     */
    private final DBHandler dbHandler;

    /**
     * JdbcTemplate for executing SQL queries.
     */
    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Constructor for CsvService.
     * @param rowRepository the repository for managing Row entities
     * @param dbHandler the handler for database operations
     */
    @Autowired
    public CsvService(RowRepository rowRepository, DBHandler dbHandler) {
        this.rowRepository = rowRepository;
        this.dbHandler = dbHandler;
    }

    /**
     * Retrieves the last ID from a specified table.
     * @param tableName the name of the table from which to retrieve the last ID
     * @return the last ID as a String
     */
    public String getLastId(String tableName) {
        return dbHandler.getLastId(tableName);
    }

    /**
     * Inserts data from a CSV file into a specified table.
     * @param tableName the name of the table where the CSV data will be inserted
     * @param csvInputStream the InputStream of the CSV file to be inserted
     */
    public void insertCsvData(String tableName, InputStream csvInputStream) {
        dbHandler.insertCsvData(tableName, csvInputStream);
    }

    /**
     * Creates an index on a specified column of a table.
     * @param tableName the name of the table where the index will be created
     * @param columnName the name of the column on which the index will be created
     */
    public void createIndex(String tableName, String columnName) {
        dbHandler.createIndex(tableName, columnName);
    }

    /**
     * Retrieves the last N rows from a specified table.
     * @param tableName the name of the table from which to retrieve the last N rows
     * @param numberOfRows the number of rows to retrieve
     * @return a ResultSet containing the last N rows
     */
    public ResultSet getLastNRows(String tableName, int numberOfRows) {
        return dbHandler.getLastNRows(tableName, numberOfRows);
    }

    /**
     * Retrieves rows from a specified table within a range defined by fromId and toId.
     * @param tableName the name of the table from which to retrieve rows
     * @param numberOfRows the maximum number of rows to retrieve
     * @param fromId the starting ID for the range
     * @param toId the ending ID for the range
     * @return a list of maps representing the rows retrieved
     */
    public List<Map<String, Object>> getRowsFromTo(String tableName, int numberOfRows, String fromId, String toId) {
        return getDataFromTo(tableName, numberOfRows, fromId, toId);
    }

    /**
     * Retrieves data from a specified table within a range defined by from and to.
     * @param tableName the name of the table from which to retrieve data
     * @param count the maximum number of rows to retrieve
     * @param from the starting ID for the range
     * @param to the ending ID for the range
     * @return a list of maps representing the data retrieved
     */
    public List<Map<String, Object>> getDataFromTo(String tableName, long count, String from, String to) {
        String query = "SELECT * FROM " + tableName + " WHERE id >= ? AND id <= ? LIMIT ?";
        return jdbcTemplate.queryForList(query, from, to, count);
    }
}
