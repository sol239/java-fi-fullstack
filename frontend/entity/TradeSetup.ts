export interface TradeSetup {
  balance: number;
  leverage: number;
  fee: number;
  takeProfit: number;
  stopLoss: number;
  amount: number;
  riskReward: number;
  maxTrades: number;
  delaySeconds: number;
  dateRestriction: string;
  maxOpenedTrades: number;
  tradeLifeSpanSeconds: number;
}