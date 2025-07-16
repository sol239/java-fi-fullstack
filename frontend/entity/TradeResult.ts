import type { TradeStrategy } from "./TradeStrategy";

export interface TradeResult {
  openPrice: number;
  takePrice: number;
  stopPrice: number;
  closePrice: number;
  amount: number;
  assetName: string;
  openTime: string;
  closeTime: string;
  PnL: number;
  strategy: TradeStrategy;
  openTimestamp: number;
  closeTimestamp: number;
}