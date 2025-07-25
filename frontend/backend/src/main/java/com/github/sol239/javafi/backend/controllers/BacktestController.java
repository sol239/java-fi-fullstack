package com.github.sol239.javafi.backend.controllers;

import com.github.sol239.javafi.backend.entity.BacktestResult;
import com.github.sol239.javafi.backend.services.BacktestService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/backtest")
public class BacktestController {
    private static final Logger logger = LoggerFactory.getLogger(BacktestController.class);

    private final BacktestService backtestService;

    public BacktestController(BacktestService backtestService) {
        this.backtestService = backtestService;
    }


    @PostMapping("run")
    public BacktestResult runBacktest(
            @RequestParam String tableName,
            @RequestParam String balance,
            @RequestParam String leverage,
            @RequestParam String fee,
            @RequestParam String takeProfit,
            @RequestParam String stopLoss,
            @RequestParam String amount,
            @RequestParam String maxTrades,
            @RequestParam String delaySeconds,
            @RequestParam String dateRestriction,
            @RequestParam String tradeLifeSpanSeconds,
            @RequestParam String openClause,
            @RequestParam String closeClause,
            @RequestParam String stopLossEnabled,
            @RequestParam String takeProfitEnabled
    ) {
        logger.info("POST:api/backtest/run BacktestController.runBacktest() called for table: {}", tableName);
        BacktestResult result = backtestService.runBackTest(
                tableName,
                balance,
                leverage,
                fee,
                takeProfit,
                stopLoss,
                amount,
                maxTrades,
                delaySeconds,
                dateRestriction,
                tradeLifeSpanSeconds,
                openClause,
                closeClause,
                stopLossEnabled,
                takeProfitEnabled
        );

        logger.info("Backtest result: " + result.backtestSummary());

        return result;

    }

}
