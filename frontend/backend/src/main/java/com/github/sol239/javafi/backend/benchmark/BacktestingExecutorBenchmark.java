package com.github.sol239.javafi.backend.benchmark;

import com.github.sol239.javafi.backend.entity.BacktestResult;
import com.github.sol239.javafi.backend.utils.backtesting.BacktestingExecutor;
import com.github.sol239.javafi.backend.utils.backtesting.Setup;
import com.github.sol239.javafi.backend.utils.backtesting.Strategy;
import com.github.sol239.javafi.backend.utils.database.DBHandler;
import com.github.sol239.javafi.backend.utils.instrument.InstrumentExecutor;
import org.openjdk.jmh.annotations.*;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.SingleShotTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
@Fork(value = 1, warmups = 0)
@Warmup(iterations = 3)
@Measurement(iterations = 3)
public class BacktestingExecutorBenchmark {

    @Benchmark
    public void runBacktestBenchmark () {
        String tableName = "BENCHMARK_TABLE";
        DBHandler handler = new DBHandler("jdbc:h2:file:./data/mydb");

        InputStream is;
        try {
            is = new FileInputStream("assets/csv/BTCUSD_1D.csv");

        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + e.getMessage());
            return;
        }

        handler.insertCsvData(tableName, is);
        handler.createIndex(tableName, "id");
        handler.createIndex(tableName, "DATE");

        String instrumentConsoleString = "rsi:14";
        String[] parts = instrumentConsoleString.split(":");
        String[] args = parts[1].split(",");
        Double[] doubleArgs = new Double[args.length];
        for (int i = 0; i < args.length; i++) {
            doubleArgs[i] = Double.parseDouble(args[i]);
        }
        String instrumentName = parts[0].trim();
        Map<String, Double[]> instrumentArgs = new HashMap<>();
        instrumentArgs.put(instrumentName, doubleArgs);

        InstrumentExecutor instrumentExecutor = new InstrumentExecutor(handler);
        instrumentExecutor.runInstrument(tableName, instrumentArgs);


        double riskReward = 1;

        double balance = 10000;
        double leverage = 3;
        double fee = 0.00025;
        double takeProfit = 1.05;
        double stopLoss = 0.95;
        double amount = 500;
        int maxTrades = 1;
        int delaySeconds = 10000;
        String dateRestriction = "";
        int tradeLifeSpanSeconds = 10000000;

        boolean takeProfitEnabled = false;
        boolean stopLossEnabled = true;

        String openClause = "WHERE rsi_14_ins_ <= 30"; // RSI overbought
        String closeClause =  "WHERE rsi_14_ins_ >= 70"; // RSI oversold

        com.github.sol239.javafi.backend.utils.backtesting.Setup setup = new Setup(balance, leverage,fee, takeProfit, stopLoss, amount,
                riskReward, maxTrades, delaySeconds, dateRestriction, tradeLifeSpanSeconds);
        Strategy strategy = new Strategy(openClause, closeClause, takeProfitEnabled, stopLossEnabled,  setup);

        DataSource dataSource = handler.getDataSource(); // Přidejte getter do DBHandler
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        BacktestingExecutor backtestingExecutor = new BacktestingExecutor(handler, jdbcTemplate);

        backtestingExecutor.clearStrategies();

        backtestingExecutor.addStrategy(strategy);

        // Also does not have to be executed each time but it is quite fast.
        backtestingExecutor.createStrategiesColumns(tableName);



        backtestingExecutor.updateStrategiesColumns(tableName);
        BacktestResult result =  backtestingExecutor.run(tableName, setup.tradeLifeSpanSeconds, strategy.takeProfit, strategy.stopLoss, "C:/Users/David/Desktop/result.json", setup.dateRestriction);
        handler.deleteTable(tableName);
    }
}
