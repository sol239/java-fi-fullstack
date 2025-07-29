package com.github.sol239.javafi.backend.services;

import com.github.sol239.javafi.backend.utils.database.DBHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service for managing database tables.
 * Provides methods to retrieve table names, delete tables, and clean tables.
 */
@Service
public class TableService {

    /**
     * Database handler for executing SQL operations.
     */
    private final DBHandler dbHandler;

    /**
     * Constructor for TableService.
     * @param dbHandler the database handler to be used for database operations
     */
    @Autowired
    public TableService(DBHandler dbHandler) {
        this.dbHandler = dbHandler;
    }

    /**
     * Retrieves the names of all OHLCV tables in the database.
     * Excludes certain system tables and metadata tables.
     * @return a list of table names
     */
    public List<String> getTableNames() {
        List<String> tableNames = dbHandler.getAllTables();
        tableNames.removeIf(tableName ->
                tableName.equalsIgnoreCase("CHART") ||
                        tableName.equalsIgnoreCase("INSTRUMENT") ||
                        tableName.equalsIgnoreCase("USER_ROLES") ||
                        tableName.toUpperCase().endsWith("_META")
        );

        tableNames.forEach(tableName -> {System.out.println("Table: " + tableName);});

        return tableNames;
    }

    /**
     * Deletes a table and its associated metadata table from the database.
     * @param tableName the name of the table to be deleted
     */
    public void deleteTable(String tableName) {
        dbHandler.deleteTable(tableName);
        dbHandler.deleteTable(tableName + "_META");
    }

    /**
     * Cleans a specified table by removing all its data.
     * @param tableName the name of the table to be cleaned
     */
    public void cleanTable(String tableName) {
        dbHandler.clean(tableName);
    }
}

