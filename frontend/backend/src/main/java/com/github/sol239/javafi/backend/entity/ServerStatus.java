package com.github.sol239.javafi.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Represents the status of the server, including memory usage, uptime, and active threads.
 */
@Data
@AllArgsConstructor
public class ServerStatus {

    /**
     * The amount of free memory available in the Java Virtual Machine.
     */
    private long freeMemory;

    /**
     * The uptime of the Java Virtual Machine in milliseconds.
     */
    private long uptime;

    /**
     * The number of active threads in the Java Virtual Machine.
     */
    private int activeThreads;

    /**
     * The total amount of memory currently available to the Java Virtual Machine.
     */
    private long totalMemory;

    /**
     * The maximum amount of memory that the Java Virtual Machine will attempt to use.
     */
    private long maxMemory;
}