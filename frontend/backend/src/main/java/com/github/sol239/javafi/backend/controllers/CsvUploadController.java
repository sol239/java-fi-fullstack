package com.github.sol239.javafi.backend.controllers;

import com.github.sol239.javafi.backend.services.CsvService;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.stream.Collectors;

import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/api/csv")
public class CsvUploadController {

    private final CsvService csvService;

    public CsvUploadController(CsvService csvService) {
        this.csvService = csvService;
    }

    @PostMapping("/upload")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> uploadCsv(
            @RequestParam("tableName") String tableName,
            @RequestParam("file") MultipartFile file) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("FAIL - File is empty.");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = (authentication != null) ? authentication.getName() : "anonymous";


        System.out.println("Soubor " + file.getOriginalFilename() + " byl úspěšně přijat na server. Od uživatele: " + username + ", pro tabulku: " + tableName);
        List<String> roles = authentication.getAuthorities()
                .stream()
                .map(a -> a.getAuthority())
                .collect(Collectors.toList());

        System.out.println("Role uživatele: " + roles);
        try (InputStream is = file.getInputStream()) {
            csvService.insertCsvData(tableName, is);
            return ResponseEntity.ok("SUCCESS - Data inserted into table: " + tableName);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("FAIL - Error processing file: " + e.getMessage());
        }
    }
}
