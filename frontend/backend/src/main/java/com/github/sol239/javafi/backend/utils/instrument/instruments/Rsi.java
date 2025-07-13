package com.github.sol239.javafi.backend.utils.instrument.instruments;


import com.github.sol239.javafi.utils.instrument.JavaInstrument;
import com.google.auto.service.AutoService;

import java.util.HashMap;
import java.util.List;

/**
 * Implementation of Relative Strength Index (RSI) indicator.
 * @see <a href="https://www.investopedia.com/terms/r/rsi.asp">Investopedia</a>
 */
@AutoService(JavaInstrument.class)
public class Rsi implements JavaInstrument {

    /**
     * Get the column names needed to run the instrument = columns which will be fetched from the table.
     * @return the column names
     */
    @Override
    public String[] getColumnNames() {
        return new String[]{"close"};
    }

    /**
     * Get the name of the instrument.
     * So the instrument can be identified by its given name and not by the class name.
     * @return the name of the instrument
     */
    @Override
    public String getName() {
        return "rsi";
    }

    /**
     * Get the description of the instrument.
     * @return the description of the instrument
     */
    @Override
    public String getDescription() {
        return "The relative strength index (RSI) is a momentum indicator that measures the magnitude of recent price changes to evaluate overbought or oversold conditions in the price of a stock or other asset." + "\n" +
                "Usage: rsi:period\n";
    }

    /**
     * Calculates the value which be inserted into the column
     * @param prices the prices of the instrument - key is the column name, value is the list of prices - values
     * @param params the parameters for the instrument
     * @return the value to be inserted into the column
     */
    @Override
    public double updateRow(HashMap<String, List<Double>> prices, Double... params) {
        int period = params[0].intValue();

        if (prices == null) {
            return 0;
        }

        if (prices.values().stream().findFirst().map(List::size).orElse(0) < period) {
            return 0;
        }

        double gainSum = 0, lossSum = 0;

        for (int i = 1; i < period; i++) {
            double change = prices.get("close").get(i) - prices.get("close").get(i - 1);
            if (change > 0) {
                gainSum += change;
            } else {
                lossSum += Math.abs(change);
            }
        }

        double avgGain = gainSum / period;
        double avgLoss = lossSum / period;

        for (int i = period + 1; i < prices.size(); i++) {
            double change = prices.get("close").get(i) - prices.get("close").get(i - 1);
            double gain = change > 0 ? change : 0;
            double loss = change < 0 ? Math.abs(change) : 0;

            avgGain = ((avgGain * (period - 1)) + gain) / period;
            avgLoss = ((avgLoss * (period - 1)) + loss) / period;
        }

        double rs = avgLoss == 0 ? Double.POSITIVE_INFINITY : avgGain / avgLoss;
        return 100 - (100 / (1 + rs));
    }
}
