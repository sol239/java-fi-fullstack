package com.github.sol239.javafi.backend.utils.backtesting;

/**
 * Represents a summary of a backtest, including the number of winning and losing trades,
 * @param winningTradesCount amount of winning trades
 * @param losingTradesCount amount of losing trades
 * @param totalTradesCount total number of trades
 * @param winrate winrate of the strategy as a percentage
 * @param profit total profit made during the backtest
 */
public record BacktestSummary(Integer winningTradesCount,
                              Integer losingTradesCount,
                              Integer totalTradesCount,
                              Double winrate,
                              Double profit) {}
