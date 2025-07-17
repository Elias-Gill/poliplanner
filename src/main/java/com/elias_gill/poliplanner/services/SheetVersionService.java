package com.elias_gill.poliplanner.services;

import com.elias_gill.poliplanner.models.SheetVersion;
import com.elias_gill.poliplanner.repositories.SheetVersionRepository;

import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SheetVersionService {

    @Autowired
    private SheetVersionRepository repository;

    @Transactional
    public SheetVersion create(String fileName, String url) {
        return repository.save(new SheetVersion(fileName, url));
    }

    public SheetVersion findLatest() {
        return repository.findFirstByOrderByParsedAtDesc();
    }

    public Boolean hasNewUpdates() {
        SheetVersion latest = findLatest();
        if (latest == null) {
            return false;
        }

        if (latest.getParsedAt() == null) {
            return false;
        }

        LocalDate parseDate = latest.getParsedAt().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        // Notifica solo 2 dias despues de la fecha de parseo del ultimo excel
        long daysBetween = ChronoUnit.DAYS.between(parseDate, LocalDate.now());
        return daysBetween <= 2;
    }
}
