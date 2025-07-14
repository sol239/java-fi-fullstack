package com.github.sol239.javafi.backend.controllers;

import com.github.sol239.javafi.backend.services.CsvService;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
    public ResponseEntity<String> uploadCsv(
            @RequestParam("tableName") String tableName,
            @RequestParam("file") MultipartFile file) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("FAIL - File is empty.");
        }

        System.out.println("Soubor " + file.getOriginalFilename() + " byl úspěšně přijat na server.");

        try (InputStream is = file.getInputStream()) {
            csvService.insertCsvData(tableName, is);
            return ResponseEntity.ok("SUCCESS - Data inserted into table: " + tableName);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("FAIL - Error processing file: " + e.getMessage());
        }
    }
}
