package com.github.sol239.javafi.backend.controllers;

import com.github.sol239.javafi.backend.auth.AuthUtil;
import com.github.sol239.javafi.backend.entity.User;
import com.github.sol239.javafi.backend.repositories.ChartJdbcRepository;
import com.github.sol239.javafi.backend.services.CsvService;
import com.github.sol239.javafi.backend.services.TableService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

@RestController
@RequestMapping("/api/tables")
public class TableController {

    private static final Logger logger = LoggerFactory.getLogger(TableController.class);

    private final TableService tableService;
    private final CsvService csvService;

    @Autowired
    public TableController(TableService tableService, CsvService csvService) {
        this.tableService = tableService;
        this.csvService = csvService;
    }

    @GetMapping
    public List<String> getTableNames() {
        String username =  AuthUtil.getUsername();

        List<String> tableNames = tableService.getTableNames();
        logger.info("GET:api/tables/ TableController.getTableNames() returned {} tables for user: {}", tableNames.size(), username);
        return tableNames;
    }

    @GetMapping("/last")
    public String getLastId(@RequestParam String tableName) {

        String username =  AuthUtil.getUsername();
        String result = csvService.getLastId(tableName);
        logger.info("GET:api/tables/last/ TableController.getLastId() returned {} table for user: {}", result, username);
        return result;
    }

    @GetMapping("/meta")
    public List<User> getMetaTables() {
        return null;
    }
}
