package com.github.sol239.javafi.backend.dto;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for chart data.
 * Represents a single data point in a financial chart.
 */
public class ChartDTO {
    /**
     * Unique identifier for the chart data point.
     */
    public long id;

    /**
     * Timestamp of the chart data point in milliseconds since epoch.
     */
    public long timestamp;

    /**
     * Open price of the financial instrument at the time of the data point.
     */
    public double open;

    /**
     * High price of the financial instrument at the time of the data point.
     */
    public double high;

    /**
     * Low price of the financial instrument at the time of the data point.
     */
    public double low;

    /**
     * Close price of the financial instrument at the time of the data point.
     */
    public double close;

    /**
     * Volume of the financial instrument traded at the time of the data point.
     */
    public double volume;

    /**
     * Date and time of the chart data point.
     */
    public LocalDateTime date;

    /**
     * Constructor to create a ChartDTO instance.
     * @param id Unique identifier for the chart data point.
     * @param timestamp Timestamp of the chart data point in milliseconds since epoch.
     * @param open Open price of the financial instrument at the time of the data point.
     * @param high High price of the financial instrument at the time of the data point.
     * @param low Low price of the financial instrument at the time of the data point.
     * @param close Close price of the financial instrument at the time of the data point.
     * @param volume Volume of the financial instrument traded at the time of the data point.
     * @param date Date and time of the chart data point.
     */
    public ChartDTO(long id, long timestamp, double open, double high, double low, double close, double volume, LocalDateTime date) {
        this.id = id;
        this.timestamp = timestamp;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
        this.date = date;
    }
}
