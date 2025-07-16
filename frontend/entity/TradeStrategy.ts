import type { TradeSetup } from "./TradeSetup";

export interface TradeStrategy {
  openClause: string;
  closeClause: string;
  takeProfit: boolean;
  stopLoss: boolean;
  setup: TradeSetup;
}