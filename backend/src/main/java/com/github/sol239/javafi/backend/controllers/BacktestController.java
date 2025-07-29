package com.github.sol239.javafi.backend.controllers;

import com.github.sol239.javafi.backend.utils.backtesting.BacktestResult;
import com.github.sol239.javafi.backend.services.BacktestService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * BacktestController handles requests related to backtesting trading strategies.
 * It provides an endpoint to run a backtest with specified parameters.
 */
@RestController
@RequestMapping("/api/backtest")
public class BacktestController {

    /**
     * Logger for BacktestController.
     * Used to log information and errors related to backtesting operations.
     */
    private static final Logger logger = LoggerFactory.getLogger(BacktestController.class);

    /**
     * Service for handling backtesting operations.
     */
    private final BacktestService backtestService;

    /**
     * Constructor for BacktestController.
     * Initializes the BacktestService.
     *
     * @param backtestService the service to handle backtesting operations
     */
    public BacktestController(BacktestService backtestService) {
        this.backtestService = backtestService;
    }

    /**
     * Runs a backtest with the specified parameters.
     *
     * @param tableName            the name of the database table to use for backtesting
     * @param balance              the initial balance for the backtest
     * @param leverage             the leverage to use for the backtest
     * @param fee                  the trading fee to apply during the backtest
     * @param takeProfit           the take profit value for the backtest
     * @param stopLoss             the stop loss value for the backtest
     * @param amount               the amount to trade in each transaction
     * @param maxTrades            the maximum number of trades to execute in the backtest
     * @param delaySeconds         the delay in seconds between trades
     * @param dateRestriction      the date restriction for the backtest (format: YYYY-MM-DD)
     * @param tradeLifeSpanSeconds the lifespan of each trade in seconds
     * @param openClause           SQL WHERE clause for opening trades
     * @param closeClause          SQL WHERE clause for closing trades
     * @param stopLossEnabled      flag to indicate if stop loss is enabled
     * @param takeProfitEnabled    flag to indicate if take profit is enabled
     * @return BacktestResult containing the results of the backtest
     */
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
        return result;
    }

}
