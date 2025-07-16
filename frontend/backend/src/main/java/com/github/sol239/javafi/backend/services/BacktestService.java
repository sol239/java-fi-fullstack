package com.github.sol239.javafi.backend.services;

import com.github.sol239.javafi.backend.controllers.BacktestResult;
import com.github.sol239.javafi.backend.utils.DataObject;
import com.github.sol239.javafi.backend.utils.backtesting.BacktestingExecutor;
import com.github.sol239.javafi.backend.utils.backtesting.Setup;
import com.github.sol239.javafi.backend.utils.backtesting.Strategy;
import org.springframework.stereotype.Service;

@Service
public class BacktestService {
    private final BacktestingExecutor backtestingExecutor;

    public BacktestService(BacktestingExecutor backtestingExecutor) {
        this.backtestingExecutor = backtestingExecutor;
    }

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

        double riskReward = 1;
        Setup setup = new Setup(Double.parseDouble(balance), Double.parseDouble(leverage), Double.parseDouble(fee), Double.parseDouble(takeProfit), Double.parseDouble(stopLoss), Double.parseDouble(amount), riskReward, Integer.parseInt(maxTrades), Integer.parseInt(delaySeconds), dateRestriction, Integer.parseInt(tradeLifeSpanSeconds));
        Strategy strategy = new Strategy(openClause, closeClause, setup);

        backtestingExecutor.clearStrategies();

        backtestingExecutor.addStrategy(strategy);
        backtestingExecutor.createStrategiesColumns(tableName);

        backtestingExecutor.updateStrategiesColumns(tableName);   // TODO: does not have to be executed each time
        BacktestResult result =  backtestingExecutor.run(tableName, setup.tradeLifeSpanSeconds, strategy.takeProfit, strategy.stopLoss, "C:/Users/David/Desktop/result.json", setup.dateRestriction);
        return result;
    }

}
