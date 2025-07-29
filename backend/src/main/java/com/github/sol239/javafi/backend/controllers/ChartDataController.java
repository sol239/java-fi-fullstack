package com.github.sol239.javafi.backend.controllers;

import com.github.sol239.javafi.backend.dto.ChartDTO;
import com.github.sol239.javafi.backend.services.CsvService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller for handling chart data requests.
 */
@RestController
@RequestMapping("/api")
public class ChartDataController {

    /**
     * Logger for logging information and errors.
     */
    private static final Logger logger = LoggerFactory.getLogger(ChartDataController.class);

    /**
     * The number of rows to fetch from the database for chart data.
     */
    public static final int ROW_COUNT = 200;

    /**
     * Service for handling CSV data operations.
     */
    @Autowired
    private CsvService csvService;

    /**
     * Fetches chart data for a specific table from id1 to id2.
     * @param table the name of the table to fetch data from
     * @param id1 the starting id for the data range
     * @param id2 the ending id for the data range
     * @return a list of ChartDTO objects containing the chart data
     */
    @GetMapping("/between")
    public List<ChartDTO> getDataBetween(@RequestParam String table, @RequestParam String id1,
                                         @RequestParam String id2) {
        logger.info("GET:api/between ChartDataController.getDataBetween() called for table: {} between ids: {} and {}", table, id1, id2);
        List<Map<String, Object>> rows = csvService.getDataFromTo(table, ROW_COUNT, id1, id2);
        List<ChartDTO> result = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            long id = ((Number) row.get("id")).longValue();
            long timestamp = ((Number) row.get("timestamp")).longValue();
            double open = ((Number) row.get("open")).doubleValue();
            double high = ((Number) row.get("high")).doubleValue();
            double low = ((Number) row.get("low")).doubleValue();
            double close = ((Number) row.get("close")).doubleValue();
            double volume = ((Number) row.get("volume")).doubleValue();
            LocalDateTime date = row.get("date") instanceof LocalDateTime ? (LocalDateTime) row.get("date") : null;
            result.add(new ChartDTO(id, timestamp, open, high, low, close, volume, date));
        }
        return result;
    }


    /**
     * Fetches the last N rows of chart data from a specific table.
     * @param table the name of the table to fetch data from
     * @return a list of ChartDTO objects containing the last N rows of chart data
     */
    @GetMapping("/lastRows")
    public List<ChartDTO> getLastRows(@RequestParam String table) {
        logger.info("GET:api/data ChartDataController.getLastRows() called for table: {}", table);
        List<ChartDTO> result = new ArrayList<>();
        try {
            ResultSet rs = csvService.getLastNRows(table, ROW_COUNT);
            while (rs != null && rs.next()) {
                long id = rs.getLong("id");
                long timestamp = rs.getLong("timestamp");
                double open = rs.getDouble("open");
                double high = rs.getDouble("high");
                double low = rs.getDouble("low");
                double close = rs.getDouble("close");
                double volume = rs.getDouble("volume");
                LocalDateTime date = rs.getObject("date", LocalDateTime.class);
                result.add(new ChartDTO(id, timestamp, open, high, low, close, volume, date));
            }
        } catch (Exception e) {
            logger.error("Error fetching data from table: {}", table, e);
        }
        return result;
    }

}
