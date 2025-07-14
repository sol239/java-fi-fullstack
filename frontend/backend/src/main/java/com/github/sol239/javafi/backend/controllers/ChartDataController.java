package com.github.sol239.javafi.backend.controllers;

import com.github.sol239.javafi.backend.entity.Chart;
import com.github.sol239.javafi.backend.repositories.ChartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // Allow CORS for Nuxt frontend
public class ChartDataController {

    @Autowired
    private ChartRepository chartRepository;

    @GetMapping("/data")
    public List<?> getChartData(@RequestParam String table) {
        // Fetch chart data by extraData field matching table name
        List<Chart> charts = chartRepository.findByExtraData(table);

        // Map entity to Lightweight Charts compatible format for line series:
        // {
        //   time: number (Unix timestamp in seconds),
        //   value: number
        // }

        return charts.stream().map(chart -> {
            return new ChartDTO(
                    chart.getTimestamp() / 1000, // convert millis to seconds
                    chart.getClose() // using close price as the line value
            );
        }).collect(Collectors.toList());
    }

    // DTO for line chart data transfer
    private static class ChartDTO {
        public long time;
        public double value;

        public ChartDTO(long time, double value) {
            this.time = time;
            this.value = value;
        }
    }
}

