package com.github.sol239.javafi.backend.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

/**
 * Represents a row in the database containing financial data.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
public class Row {

    /**
     * Unique identifier for the row.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false)
    private Long id;

    /**
     * Timestamp of the row data.
     */
    @Column(nullable = false)
    private Long timestamp;

    /**
     * The name of the table this row belongs to.
     */
    @Column(nullable = false)
    private double open;

    /**
     * The high price of the financial instrument for this row.
     */
    @Column(nullable = false)
    private double high;

    /**
     * The low price of the financial instrument for this row.
     */
    @Column(nullable = false)
    private double low;

    /**
     * The close price of the financial instrument for this row.
     */
    @Column(nullable = false)
    private double close;

    /**
     * The volume of the financial instrument for this row.
     */
    @Column(nullable = false)
    private double volume;

    /**
     * The date and time of the row data.
     */
    @Column(nullable = false)
    private LocalDateTime date;

    /**
     * Additional data associated with the row, stored as a JSON string.
     */
    @Column(columnDefinition = "TEXT")
    private String extraData;
}
