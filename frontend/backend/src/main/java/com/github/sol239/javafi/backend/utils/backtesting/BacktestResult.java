package com.github.sol239.javafi.backend.utils.backtesting;

import java.util.List;

/**
 * Represents the result of a backtest, including a summary and all trades.
 * @param backtestSummary the summary of the backtest results
 * @param allTrades the list of all trades executed during the backtest
 */
public record BacktestResult(BacktestSummary backtestSummary, List<Trade> allTrades) {
}
