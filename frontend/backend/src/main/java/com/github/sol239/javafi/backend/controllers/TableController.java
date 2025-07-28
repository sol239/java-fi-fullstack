package com.github.sol239.javafi.backend.controllers;

import com.github.sol239.javafi.backend.auth.AuthUtil;
import com.github.sol239.javafi.backend.entity.Chart;
import com.github.sol239.javafi.backend.entity.User;
import com.github.sol239.javafi.backend.repositories.ChartJdbcRepository;
import com.github.sol239.javafi.backend.services.CsvService;
import com.github.sol239.javafi.backend.services.TableService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

@RestController
@RequestMapping("/api/tables")
public class TableController {

    private static final Logger logger = LoggerFactory.getLogger(TableController.class);

    private final TableService tableService;
    private final CsvService csvService;
    private final ChartJdbcRepository chartJdbcRepository;

    @Autowired
    public TableController(ChartJdbcRepository chartJdbcRepository, TableService tableService, CsvService csvService) {
        this.chartJdbcRepository = chartJdbcRepository;
        this.tableService = tableService;
        this.csvService = csvService;
    }

    @GetMapping
    public List<String> getTableNames() {
        String username = AuthUtil.getUsername();

        List<String> tableNames = tableService.getTableNames();
        logger.info("GET:api/tables/ TableController.getTableNames() returned {} tables for user: {}", tableNames.size(), username);
        return tableNames;
    }

    @GetMapping("/last")
    public String getLastId(@RequestParam String tableName) {

        String username = AuthUtil.getUsername();
        String result = csvService.getLastId(tableName);
        logger.info("GET:api/tables/last/ TableController.getLastId() returned {} table for user: {}", result, username);
        return result;
    }

    @GetMapping("/meta")
    public List<Chart> getMetaTables() {
        logger.info("GET:api/tables/meta/ TableController.getMetaTables() called");
        return this.chartJdbcRepository.findAll();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{tableName}")
    public void deleteTable(@PathVariable String tableName) {
        logger.info("DELETE:api/tables/delete/ TableController.deleteTable() called for table: {}", tableName);
        String username = AuthUtil.getUsername();
        tableService.deleteTable(tableName);
        logger.info("Table with name: {} deleted by user: {}", tableName, username);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/clean/{tableName}")
    public void cleanTable(@PathVariable String tableName) {
        logger.info("PATCH:api/tables/delete/ TableController.cleanTable() called for table: {}", tableName);
        String username = AuthUtil.getUsername();
        tableService.cleanTable(tableName);
        logger.info("Table with name: {} cleaned by user: {}", tableName, username);
    }
}
