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

import static com.fasterxml.jackson.databind.type.LogicalType.DateTime;

@RestController
@RequestMapping("/api")
public class ChartDataController {


    public static final int ROW_COUNT = 200;

    @Autowired
    private ChartRepository chartRepository;

    @Autowired
    private CsvService csvService;

    @GetMapping("/betweendata")
    public List<ChartDTO> getDataBetween(@RequestParam String table, @RequestParam String id1,
                                         @RequestParam String id2) {

        System.out.println("Fetching last " + ROW_COUNT + " rows from table: " + table +
                " between ids: " + id1 + " and " + id2  );

        List<ChartDTO> result = new ArrayList<>();
        try {
            ResultSet rs = csvService.getRowsFromTo(table, ROW_COUNT, id1, id2);
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
            System.out.println("Error fetching data from table: " + table);
            e.printStackTrace();
        }
        return result;
    }


    @GetMapping("/data")
    public List<ChartDTO> getLastRows(@RequestParam String table) {

        System.out.println("Fetching last " + ROW_COUNT + " rows from table: " + table);

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
            System.out.println("Error fetching data from table: " + table);
            e.printStackTrace();
        }
        return result;
    }

    // DTO for line chart data transfer
    public static class ChartDTO {
        public long id;
        public long timestamp;
        public double open;
        public double high;
        public double low;
        public double close;
        public double volume;
        public LocalDateTime date;

        public ChartDTO(long id, long timestamp, double open, double high, double low, double close, double volume, LocalDateTime date) {
            this.id = id;
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
