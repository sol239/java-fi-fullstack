package com.github.sol239.javafi.backend.controllers;

public record BacktestSummary(Integer winningTradesCount,
                              Integer losingTradesCount,
                              Integer totalTradesCount,
                              Double winrate,
                              Double profit) {}
