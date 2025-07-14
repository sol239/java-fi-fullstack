package com.github.sol239.javafi.backend.controllers;

import com.github.sol239.javafi.backend.services.TableService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

@RestController
@RequestMapping("/api/tables")
public class TableController {

    private static final Logger logger = LoggerFactory.getLogger(TableController.class);

    private final TableService tableService;

    @Autowired
    public TableController(TableService tableService) {
        this.tableService = tableService;
    }


    @GetMapping
    public List<String> getTableNames() {


        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String username = (authentication != null) ? authentication.getName() : "anonymous";

        logger.info("getTableNames from user: {}", username);

        List<String> tableNames = tableService.getTableNames();

        logger.info("{} table names returned for user: {}", tableNames.size(), username);

        return tableNames;
    }
}
