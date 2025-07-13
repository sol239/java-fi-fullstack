package com.github.sol239.javafi.backend.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChartsController {
    @GetMapping("/charts/data")
    public String getChartData() {
        return ":)";
    }
}