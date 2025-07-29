import type { BacktestSummary } from "./BacktestSummary";
import type { TradeResult } from "./TradeResult";

export interface BacktestResult {
  summary: BacktestSummary;
  allTrades: TradeResult[];
}
