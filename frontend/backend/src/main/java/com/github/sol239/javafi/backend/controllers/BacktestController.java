package com.github.sol239.javafi.backend.controllers;

import com.github.sol239.javafi.backend.entity.BacktestResult;
import com.github.sol239.javafi.backend.services.BacktestService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/backtest")
public class BacktestController {

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
        System.out.println("Running backtest for table: " + tableName);
        BacktestResult result = backtestService.runBackTest(
                tableName, // This should be replaced with the actual table name if needed
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

        System.out.println("Backtest result: " + result.backtestSummary());

        return result;

    }

}
