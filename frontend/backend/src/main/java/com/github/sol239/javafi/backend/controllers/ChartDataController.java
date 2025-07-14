package com.github.sol239.javafi.backend.controllers;

import com.github.sol239.javafi.backend.entity.Chart;
import com.github.sol239.javafi.backend.repositories.ChartRepository;
import com.github.sol239.javafi.backend.services.CsvService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ChartDataController {


    public static final int ROW_COUNT = 200;

    @Autowired
    private ChartRepository chartRepository;

    @Autowired
    private CsvService csvService;


    @GetMapping("/data")
    public List<ChartDTO> getLastRows(@RequestParam String table) {

        System.out.println("Fetching last " + ROW_COUNT + " rows from table: " + table);

        List<ChartDTO> result = new ArrayList<>();
        try {
            ResultSet rs = csvService.getLastNRows(table, ROW_COUNT);
            while (rs != null && rs.next()) {
                long timestamp = rs.getLong("timestamp") / 1000;
                double open = rs.getDouble("open");
                double high = rs.getDouble("high");
                double low = rs.getDouble("low");
                double close = rs.getDouble("close");
                double volume = rs.getDouble("volume");
                LocalDateTime date = rs.getObject("date", LocalDateTime.class);
                result.add(new ChartDTO(timestamp, open, high, low, close, volume, date));
            }
        } catch (Exception e) {
            System.out.println("Error fetching data from table: " + table);
            e.printStackTrace();
        }
        return result;
    }

    // DTO for line chart data transfer
    public static class ChartDTO {
        public long timestamp;
        public double open;
        public double high;
        public double low;
        public double close;
        public double volume;
        public LocalDateTime date;

        public ChartDTO(long timestamp, double open, double high, double low, double close, double volume, LocalDateTime date) {
            this.timestamp = timestamp;
            this.open = open;
            this.high = high;
            this.low = low;
            this.close = close;
            this.volume = volume;
            this.date = date;
        }
    }
}
