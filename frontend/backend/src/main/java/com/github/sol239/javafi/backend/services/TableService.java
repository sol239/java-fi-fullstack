package com.github.sol239.javafi.backend.services;

import com.github.sol239.javafi.backend.utils.database.DBHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TableService {

    private final DBHandler dbHandler;

    @Autowired
    public TableService(DBHandler dbHandler) {
        this.dbHandler = dbHandler;
    }

    public List<String> getTableNames() {
        return dbHandler.getAllTables();
    }
}

