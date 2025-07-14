package com.github.sol239.javafi.backend.services;

import com.github.sol239.javafi.backend.repositories.ChartRepository;
import com.github.sol239.javafi.backend.utils.database.DBHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.sql.ResultSet;

@Service
public class CsvService {

    private final ChartRepository chartRepository;
    private final DBHandler dbHandler;

    @Autowired
    public CsvService(ChartRepository chartRepository, DBHandler dbHandler) {
        this.chartRepository = chartRepository;
        this.dbHandler = dbHandler;
    }

    public void insertCsvData(String tableName, InputStream csvInputStream) {
        dbHandler.insertCsvData(tableName, csvInputStream);
    }

    public ResultSet getLastNRows(String tableName, int numberOfRows) {
        return dbHandler.getLastNRows(tableName, numberOfRows);
    }
}
