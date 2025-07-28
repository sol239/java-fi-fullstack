package com.github.sol239.javafi.backend.services;

import com.github.sol239.javafi.backend.utils.database.DBHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TableService {

    private final DBHandler dbHandler;

    @Autowired
    public TableService(DBHandler dbHandler) {
        this.dbHandler = dbHandler;
    }

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
}

