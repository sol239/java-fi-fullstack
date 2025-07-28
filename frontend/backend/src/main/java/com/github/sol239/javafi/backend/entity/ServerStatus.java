package com.github.sol239.javafi.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ServerStatus {
    private long freeMemory;
    private long uptime;
    private int activeThreads;
    private long totalMemory;
    private long maxMemory;
}