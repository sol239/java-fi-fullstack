package com.github.sol239.javafi.backend.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Chart {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false)
    private Long id;

    private Long timestamp;
    private double open;
    private double high;
    private double low;
    private double close;
    private double volume;
    private LocalDateTime date;

    @Column(columnDefinition = "jsonb")
    private String extraData;
}
