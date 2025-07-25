package com.github.sol239.javafi.backend.entity;

import com.github.sol239.javafi.backend.utils.backtesting.Trade;

import java.util.List;

public record BacktestResult(BacktestSummary backtestSummary, List<Trade> allTrades) {
}
