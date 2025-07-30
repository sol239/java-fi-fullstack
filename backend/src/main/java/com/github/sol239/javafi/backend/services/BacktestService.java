package com.github.sol239.javafi.backend.services;

import com.github.sol239.javafi.backend.utils.backtesting.BacktestResult;
import com.github.sol239.javafi.backend.utils.backtesting.BacktestingExecutor;
import com.github.sol239.javafi.backend.utils.backtesting.Setup;
import com.github.sol239.javafi.backend.utils.backtesting.Strategy;
import org.springframework.stereotype.Service;

/**
 * Service class for running backtests.
 */
@Service
public class BacktestService {

    /**
     * Executor for running backtests.
     */
    private final BacktestingExecutor backtestingExecutor;

    /**
     * Constructor for BacktestService.
     *
     * @param backtestingExecutor the executor to run backtests
     */
    public BacktestService(BacktestingExecutor backtestingExecutor) {
        this.backtestingExecutor = backtestingExecutor;
    }

    /**
     * Runs a backtest with the specified parameters.
     *
     * @param tableName the name of the table to run the backtest on
     * @param balance the initial balance for the backtest
     * @param leverage the leverage to use in the backtest
     * @param fee the fee percentage for trades
     * @param takeProfit the take profit percentage
     * @param stopLoss the stop loss percentage
     * @param amount the amount to trade
     * @param maxTrades the maximum number of trades to execute
     * @param delaySeconds delay in seconds before executing trades
     * @param dateRestriction date restriction for trades (can be null)
     * @param tradeLifeSpanSeconds lifespan of each trade in seconds
     * @param openClause SQL clause for opening trades
     * @param closeClause SQL clause for closing trades
     * @param stopLossEnabled whether stop loss is enabled
     * @param takeProfitEnabled whether take profit is enabled
     * @return a BacktestResult containing the results of the backtest
     */
    public BacktestResult runBackTest(
            String tableName,
            String balance,
            String leverage,
            String fee,
            String takeProfit,
            String stopLoss,
            String amount,
            String maxTrades,
            String delaySeconds,
            String dateRestriction,
            String tradeLifeSpanSeconds,
            String openClause,
            String closeClause,
            String stopLossEnabled,
            String takeProfitEnabled) {

        double riskReward = Double.parseDouble(takeProfit) / Double.parseDouble(stopLoss);
        Setup setup = new Setup(Double.parseDouble(balance), Double.parseDouble(leverage), Double.parseDouble(fee), Double.parseDouble(takeProfit), Double.parseDouble(stopLoss), Double.parseDouble(amount), riskReward, Integer.parseInt(maxTrades), Integer.parseInt(delaySeconds), dateRestriction, Integer.parseInt(tradeLifeSpanSeconds));
        Strategy strategy =     new Strategy(openClause, closeClause, Boolean.parseBoolean(takeProfitEnabled), Boolean.parseBoolean(stopLossEnabled),  setup);

        //System.out.println("-----------------------------------------------");

        backtestingExecutor.clearStrategies();
        //System.out.println("1. strategies cleared.");

        backtestingExecutor.addStrategy(strategy);
        //System.out.println("2. strategies added.");

        backtestingExecutor.createStrategiesColumns(tableName);
        //System.out.println("3. strategies columns created.");

        backtestingExecutor.updateStrategiesColumns(tableName);
        //System.out.println("4. strategies columns updated.");

        //System.out.println("5. backtesting started.");
        BacktestResult result =  backtestingExecutor.run(tableName, setup.tradeLifeSpanSeconds, strategy.takeProfit, strategy.stopLoss, "", setup.dateRestriction);

        return result;
    }

}
