package com.github.sol239.javafi.backend.controllers;


import com.github.sol239.javafi.backend.entity.ServerStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.management.ManagementFactory;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @GetMapping("/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ServerStatus getStatus() {
        logger.info("GET:api/admin/status AdminController.getStatus() called");

        ServerStatus status = new ServerStatus(
                Runtime.getRuntime().freeMemory(),
                ManagementFactory.getRuntimeMXBean().getUptime(),
                Thread.activeCount(),
                Runtime.getRuntime().totalMemory(),
                Runtime.getRuntime().maxMemory()
        );

        System.out.println("Server Status: " + status);

        return status;
    }


}
