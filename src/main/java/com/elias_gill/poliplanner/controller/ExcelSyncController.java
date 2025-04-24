package com.elias_gill.poliplanner.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import com.elias_gill.poliplanner.excel.ExcelService;

@Controller
public class ExcelSyncController {
    @Autowired
    ExcelService service;

    @PostMapping("/sync")
    public ResponseEntity<?> syncExcel(@RequestHeader("Authorization") String authHeader) {
        String expectedKey = System.getenv("UPDATE_KEY");
        if (!authHeader.trim().equals("Bearer " + expectedKey)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            service.SyncronizeExcel();
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("No se pudo sincronizar: " + e);
        }
    }

    @GetMapping("/sync")
    public String showSyncForm() {
        // templates/sync.html
        return "sync";
    }
}
