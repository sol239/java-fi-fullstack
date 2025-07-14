/**
 * The package provides backtesting utils.
 */
package com.github.sol239.javafi.backend.utils.backtesting;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.sol239.javafi.backend.utils.DataObject;
import com.github.sol239.javafi.backend.utils.database.DBHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class BacktestingExecutor {

    /**
     * Suffix for the columns that store the open signals for the strategy
     */
    public static final String STRATEGY_OPEN_COLUMN_SUFFIX = "_stgO_";

    /**
     * Suffix for the columns that store the close signals for the strategy
     */
    public static final String STRATEGY_CLOSE_COLUMN_SUFFIX = "_stgC_";


    private List<Trade> openedTrades = new ArrayList<>();
    private List<Trade> loosingTrades = new ArrayList<>();
    private List<Trade> winningTrades = new ArrayList<>();

    private Map<Integer, Setup> setups = new HashMap<>();

    private DBHandler db;

    public List<Strategy> strategies;

    @Autowired
    public BacktestingExecutor(DBHandler dbHandler) {
        this.strategies = new ArrayList<>();
        this.db = dbHandler;
        this.db.setFetchSize(1000);
    }

    public void addStrategy(Strategy strategy) {
        this.strategies.add(strategy);
    }


    /**
     * Calculates the difference between two dates and returns true if the difference is greater than 'n' seconds.
     * @param dateString1 The first date string in the format 'yyyy-MM-dd HH:mm:ss[.SSSSSS]'
     * @param dateString2 The second date string in the format 'yyyy-MM-dd HH:mm:ss[.SSSSSS]'
     * @param n The number of seconds that the difference must be greater than
     * @return True if the difference between the two dates is greater than 'n' seconds, false otherwise
     */
    public static boolean isDifferenceGreaterThan(String dateString1, String dateString2, long n) {
        // Define the format for the date string that handles both formats (with and without microseconds)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss[.SSSSSS]");

        // Parse the strings to LocalDateTime objects
        LocalDateTime date1 = LocalDateTime.parse(dateString1, formatter);
        LocalDateTime date2 = LocalDateTime.parse(dateString2, formatter);

        // Calculate the difference in seconds between the two dates
        Duration duration = Duration.between(date1, date2);
        long differenceInSeconds = Math.abs(duration.getSeconds());

        // Return true if the difference in seconds is greater than 'n'
        return differenceInSeconds > n;
    }

    /**
     * Returns the name of the column that stores the signals for the strategy.
     * @param open True if the column stores the open signals, false if it stores the close signals
     * @return The name of the column that stores the signals for the strategy
     */
    public String getStrategyColumnName(Strategy strategy, boolean open) {
        if (open) {
            return "\"" + strategy.openClause + " - " + strategy.closeClause + STRATEGY_OPEN_COLUMN_SUFFIX + "\"";
        } else {
            return "\"" + strategy.openClause + " - " + strategy.closeClause + STRATEGY_CLOSE_COLUMN_SUFFIX + "\"";
        }
    }

    /**
     * Creates the columns in the database table that store the signals for the strategy.
     * @param tableName The name of the database table
     */
    public void createStrategyColumns(Strategy strategy, String tableName) {
        String openColumnName = getStrategyColumnName(strategy, true);
        String closeColumnName = getStrategyColumnName(strategy, false);

        // Creating open/false boolean columns
        String createOpenColumn = """
                ALTER TABLE IF EXISTS %s
                ADD COLUMN IF NOT EXISTS %s BOOLEAN DEFAULT FALSE;
                """.formatted(tableName, openColumnName);

        String createCloseColumn = """
                ALTER TABLE IF EXISTS %s
                ADD COLUMN IF NOT EXISTS %s BOOLEAN DEFAULT FALSE;
                """.formatted(tableName, closeColumnName);

        db.executeQuery(createOpenColumn);
        db.executeQuery(createCloseColumn);
        //System.out.println("Strategy columns created.");
    }

    public void createStrategiesColumns(String tableName) {
        for (Strategy strategy : strategies) {
            this.createStrategyColumns(strategy, tableName);
        }
    }

    /**
     * Updates the columns in the database table that store the signals for the strategy.
     * @param tableName The name of the database table
     */
    public void updateStrategyColumns(Strategy strategy, String tableName) {
        String openColumnName = getStrategyColumnName(strategy, true);
        String closeColumnName = getStrategyColumnName(strategy, false);

        // Update open/close columns based on the original clauses
        String updateOpenColumn = """
                UPDATE %s
                SET %s = TRUE
                %s;
                """.formatted(tableName, openColumnName, strategy.openClause);

        String updateCloseColumn = """
                UPDATE %s
                SET %s = TRUE
                %s;
                """.formatted(tableName, closeColumnName, strategy.closeClause);

        db.executeQuery(updateOpenColumn);
        db.executeQuery(updateCloseColumn);
        //System.out.println("Strategy columns updated.");
    }

    public void updateStrategiesColumns(String tableName) {
        for (Strategy strategy : strategies) {
            this.updateStrategyColumns(strategy, tableName);
        }

    }

    public DataObject run(String tableName, long tradeLifeSpanSeconds, boolean takeProfit, boolean stopLoss, String saveResultPath, String dateRestriction) {

        long t1 = System.currentTimeMillis();

        List<String> lines;

        ResultSet rs;
        try {
            rs = this.db.getResultSet(String.format("SELECT * FROM %s %s ORDER BY id", tableName, dateRestriction));
        } catch (Exception e) {
            return new DataObject(400, "backtesting", "Error getting the data from the database");
        }



        int skipRows = 1;
        int c = 0;
        try {
            while (rs.next()) {
                c++;
                if (c < skipRows) {
                    continue;
                }
                for (Strategy strategy : strategies) {
                    String openX = this.getStrategyColumnName(strategy, true).substring(1, this.getStrategyColumnName(strategy ,true).length() - 1);
                    String closeX = this.getStrategyColumnName(strategy,false).substring(1, this.getStrategyColumnName(strategy, false).length() - 1);
                    boolean open = rs.getBoolean(openX);
                    boolean close = rs.getBoolean(closeX);
                    double closePrice = rs.getDouble("close");
                    String closeTime = rs.getString("date");
                    String now = rs.getString("date");

                    // trade closing
                    Iterator<Trade> iterator = this.openedTrades.iterator();
                    while (iterator.hasNext()) {
                        Trade trade = iterator.next();
                        boolean tradeClosed = false;

                        if (closePrice <= trade.stopPrice) {



                            trade.closeTime = closeTime;
                            trade.closePrice = closePrice;
                            trade.PnL = (trade.closePrice * trade.amount - trade.openPrice * trade.amount) * strategy.setup.leverage * (1 - strategy.setup.fee);
                            if (closePrice >= trade.openPrice) {
                                winningTrades.add(trade);
                            } else {
                                loosingTrades.add(trade);
                            }
                            iterator.remove();

                            double profit = (trade.closePrice * trade.amount - trade.openPrice * trade.amount) * strategy.setup.leverage;
                            strategy.setup.balance += profit;
                            continue;
                        }

                        // It is generally good to specify that:
                        // - current close must be > than trade.open * (1 + takeProfit)
                        // - current close must be > than trade.open * (1 + fee)
                        if (close) {

                            // If takeProfit is set, takePrice must reached to close the trade.
                            if (takeProfit) {
                                if (!(closePrice >= trade.takePrice)) {
                                    continue;
                                }
                            }

                            trade.closeTime = closeTime;
                            trade.closePrice = closePrice;
                            trade.PnL = (trade.closePrice * trade.amount - trade.openPrice * trade.amount) * strategy.setup.leverage * (1 - strategy.setup.fee);

                            if (closePrice >= trade.openPrice) {
                                winningTrades.add(trade);
                            } else {
                                loosingTrades.add(trade);
                            }
                            iterator.remove();

                            double profit = (trade.closePrice * trade.amount - trade.openPrice * trade.amount) * strategy.setup.leverage;
                            strategy.setup.balance += profit;
                        }
                    }

                    Iterator<Trade> openedTradesIterator = this.openedTrades.iterator();

                    // TRADE TIME LIFESPAN CLOSER
                    if (tradeLifeSpanSeconds > 0) {
                        this.closeTrades(openedTradesIterator, strategy.setup, closePrice, closeTime, tradeLifeSpanSeconds);
                    }

                    // LONG TRADE OPENING
                    if (open && this.openedTrades.size() < strategy.setup.maxTrades && strategy.setup.balance - strategy.setup.amount >= 0) {
                        if (!this.openedTrades.isEmpty()) {
                            if (isDifferenceGreaterThan(now,this.openedTrades.get(this.openedTrades.size() - 1).openTime, strategy.setup.delaySeconds)) {
                                Trade newTrade = new Trade(closePrice, closePrice * (strategy.setup.takeProfit), closePrice * (strategy.setup.stopLoss), 0, (strategy.setup.amount * strategy.setup.leverage / closePrice), tableName, rs.getString("date"), "", strategy);
                                strategy.setup.balance -= strategy.setup.amount / closePrice;
                                this.openedTrades.add(newTrade);
                            }
                        } else  {
                            Trade newTrade = new Trade(closePrice, closePrice * (strategy.setup.takeProfit), closePrice * (strategy.setup.stopLoss), 0, (strategy.setup.amount * strategy.setup.leverage / closePrice), tableName, rs.getString("date"), "", strategy);
                            strategy.setup.balance -= strategy.setup.amount / closePrice;
                            this.openedTrades.add(newTrade);
                        }

                        if (this.openedTrades.size() > strategy.setup.maxOpenedTrades) {
                            strategy.setup.maxOpenedTrades = this.openedTrades.size();
                        }

                    /*
                    System.out.println("-----------------------------------------");
                    System.out.println("CLOSE PRICE: " + closePrice + " | " + rs.getString("rsi_14_ins_"));
                    System.out.println("Trade opened: ");
                    System.out.println(newTrade);
                    System.out.println("-----------------------------------------");
                    */
                    }
                    // TODO: SHORT TRADE OPENING
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        long t2 = System.currentTimeMillis();

        DataObject result = evaluateTrades(winningTrades, loosingTrades);
        List<Trade> allTrades = new ArrayList<>();
        allTrades.addAll(winningTrades);
        allTrades.addAll(loosingTrades);


        if (!saveResultPath.isEmpty()) {
            saveBacktesting(saveResultPath, allTrades);
        }

        return result;


        /*
        System.out.println("\n****************************************");
        System.out.println("EXECUTION TIME: " + Double.parseDouble(String.valueOf(t2 - t1)) / 1000 + " s");
        System.out.println("Trade Counts: " + this.openedTrades.size() + " | " + winningTrades.size() + " | " + loosingTrades.size());
        System.out.println("****************************************");
        */
    }

    public void saveBacktesting(String path, List<Trade> trades ) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            File outputFile = new File(path);
            objectMapper.writeValue(outputFile, trades);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeTrades(Iterator<Trade> iterator, Setup setup, double closePrice, String closeTime, long delaySeconds) {


        //System.out.println("closeTrades");

        while (iterator.hasNext()) {
            Trade trade = iterator.next();
            // 1) close if trade lifetime is greater than 2 days
            //System.out.println(closeTime);
            //System.out.println(trade.openTime);
            if (isDifferenceGreaterThan(closeTime, trade.openTime, delaySeconds
            )) {
                //System.out.println("CLOSING - TIME LIMIT");
                trade.closeTime = closeTime;
                trade.closePrice = closePrice;
                trade.PnL = (trade.closePrice * trade.amount - trade.openPrice * trade.amount) * setup.leverage * (1 - setup.fee);
                if (closePrice >= trade.openPrice) {
                    winningTrades.add(trade);
                } else {
                    loosingTrades.add(trade);
                }
                iterator.remove();
                double profit = (trade.closePrice * trade.amount - trade.openPrice * trade.amount) * setup.leverage;
                setup.balance += profit;
            }
        }
    }

    public static DataObject evaluateTrades(List<Trade> winningTrades, List<Trade> loosingTrades) {
        long winningTradesCount = winningTrades.size();
        long loosingTradesCount = loosingTrades.size();
        double winRate = (double) winningTradesCount / (winningTradesCount + loosingTradesCount);

        // Displaying trades

        List<Trade> allTrades = new ArrayList<>();
        allTrades.addAll(winningTrades);
        allTrades.addAll(loosingTrades);

        double percentProfitSum = 0;
        for (Trade trade : allTrades) {
            // print % profit
            double profitPercent = (((trade.closePrice) / trade.openPrice) * 100) - 100;
            //System.out.println(trade);
            //System.out.println("Profit: " + String.format("%.2f", profitPercent) + "%");
        }
        double _profit = 0;
        for (Trade trade : allTrades) {
            // LONG TRADE
            if (trade.takePrice > trade.openPrice) {
                double tradeProfit = (trade.closePrice * trade.amount - trade.openPrice * trade.amount) * (1 - trade.strategy.setup.fee);
                _profit += tradeProfit;
                // System.out.println("CLOSE: " + trade.closePrice + " | OPEN: " + trade.openPrice + " | PROFIT: " + tradeProfit);
            }
            // SHORT TRADE
            else if (trade.takePrice < trade.openPrice) {
                _profit += -((trade.closePrice * trade.amount - trade.openPrice * trade.amount)  * (1 - trade.strategy.setup.fee));
            }

        }


        String summary = String.format(
                "Winning trades: %d | Losing trades: %d | Total trades: %d | Win rate: %.2f%% | Profit: %.2f USD",
                winningTradesCount,
                loosingTradesCount,
                (winningTradesCount + loosingTradesCount),
                winRate * 100,
                _profit
        );

        DataObject dataObject = new DataObject(200, "backtesting", "Backtesting completed:\n" + summary);

        return dataObject;
    }

}
