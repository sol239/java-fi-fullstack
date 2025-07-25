package com.github.sol239.javafi.backend.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
public class ChartsController {
    private static final Logger logger = LoggerFactory.getLogger(ChartsController.class);

    @GetMapping("/charts/data")
    public String getChartData() {
        logger.info("GET:/charts/data ChartsController.getChartData() called");
        return ":)";
    }
}