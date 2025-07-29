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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for managing database tables.
 */
@RestController
@RequestMapping("/api/tables")
public class TableController {

    /**
     * Logger for TableController.
     */
    private static final Logger logger = LoggerFactory.getLogger(TableController.class);

    /**
     * Service for handling table operations.
     */
    private final TableService tableService;

    /**
     * Service for handling CSV file operations.
     */
    private final CsvService csvService;

    /**
     * Repository for managing chart metadata.
     */
    private final ChartJdbcRepository chartJdbcRepository;

    /**
     * Constructor for TableController.
     * @param chartJdbcRepository the repository for chart metadata
     * @param tableService the service for table operations
     * @param csvService the service for CSV file operations
     */
    @Autowired
    public TableController(ChartJdbcRepository chartJdbcRepository, TableService tableService, CsvService csvService) {
        this.chartJdbcRepository = chartJdbcRepository;
        this.tableService = tableService;
        this.csvService = csvService;
    }

    /**
     * Retrieves the names of all OHLCV tables in the database.
     * @return a list of table names
     */
    @GetMapping
    public List<String> getTableNames() {
        String username = AuthUtil.getUsername();

        List<String> tableNames = tableService.getTableNames();
        logger.info("GET:api/tables/ TableController.getTableNames() returned {} tables for user: {}", tableNames.size(), username);
        return tableNames;
    }

    /**
     * Retrieves the last ID from a specified table.
     * @param tableName the name of the table
     * @return the last ID in the table
     */
    @GetMapping("/lastId")
    public String getLastId(@RequestParam String tableName) {

        String username = AuthUtil.getUsername();
        String result = csvService.getLastId(tableName);
        logger.info("GET:api/tables/last/ TableController.getLastId() returned {} table for user: {}", result, username);
        return result;
    }

    /**
     * Retrieves metadata for all tables.
     * @return a list of Chart objects containing metadata for each table
     */
    @GetMapping("/meta")
    public List<Chart> getMetaTables() {
        logger.info("GET:api/tables/meta/ TableController.getMetaTables() called");
        return this.chartJdbcRepository.findAll();
    }

    /**
     * Deletes a specified table from the database.
     * @param tableName the name of the table to delete
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{tableName}")
    public void deleteTable(@PathVariable String tableName) {
        logger.info("DELETE:api/tables/delete/ TableController.deleteTable() called for table: {}", tableName);
        String username = AuthUtil.getUsername();
        tableService.deleteTable(tableName);
        logger.info("Table with name: {} deleted by user: {}", tableName, username);
    }

    /**
     * Cleans a specified table by removing all its data.
     * @param tableName the name of the table to clean
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/clean/{tableName}")
    public void cleanTable(@PathVariable String tableName) {
        logger.info("PATCH:api/tables/delete/ TableController.cleanTable() called for table: {}", tableName);
        String username = AuthUtil.getUsername();
        tableService.cleanTable(tableName);
        logger.info("Table with name: {} cleaned by user: {}", tableName, username);
    }

    /**
     * Uploads a CSV file to a specified table in the database.
     * @param tableName the name of the table to upload data to
     * @param description a description of the data being uploaded
     * @param assetName the name of the asset associated with the data
     * @param timeframe the timeframe of the data being uploaded
     * @param file the CSV file to upload
     * @return a ResponseEntity indicating the success or failure of the upload
     */
    @PostMapping("/upload")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> uploadCsv(
            @RequestParam("tableName") String tableName,
            @RequestParam("description") String description,
            @RequestParam("assetName") String assetName,
            @RequestParam("timeframe") String timeframe,
            @RequestParam("file") MultipartFile file) {

        if (file.isEmpty()) {
            logger.warn("POST:api/csv/upload CsvUploadController.uploadCsv() - File is empty for table: {}", tableName);
            return ResponseEntity.badRequest().body("FAIL - File is empty.");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = (authentication != null) ? authentication.getName() : "anonymous";

        logger.info("POST:api/csv/upload CsvUploadController.uploadCsv() - Soubor {} byl úspěšně přijat na server. Od uživatele: {}, pro tabulku: {}", file.getOriginalFilename(), username, tableName);
        List<String> roles = authentication.getAuthorities()
                .stream()
                .map(a -> a.getAuthority())
                .collect(Collectors.toList());

        System.out.println("Role uživatele: " + roles);
        try (InputStream is = file.getInputStream()) {
            csvService.insertCsvData(tableName, is);
            csvService.createIndex(tableName, "id");
            csvService.createIndex(tableName, "DATE");

            Chart chart = new Chart();
            chart.setName(tableName);
            chart.setAssetName(assetName);
            chart.setDescription(description);
            chart.setTimeframe(timeframe);

            chartJdbcRepository.save(chart, tableName);

            return ResponseEntity.ok("SUCCESS - Data inserted into table: " + tableName);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("FAIL - Error processing file: " + e.getMessage());
        }
    }
}
