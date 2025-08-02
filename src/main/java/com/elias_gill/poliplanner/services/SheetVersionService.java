package com.elias_gill.poliplanner.services;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import org.springframework.stereotype.Service;

import com.elias_gill.poliplanner.models.SheetVersion;
import com.elias_gill.poliplanner.repositories.SheetVersionRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SheetVersionService {
    private final SheetVersionRepository repository;

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

        LocalDate parseDate = latest.getParsedAt().toLocalDate();

        // Notifica durante 2 dias despues de la fecha de parseo del ultimo excel
        long daysBetween = ChronoUnit.DAYS.between(parseDate, LocalDate.now());
        return daysBetween <= 2;
    }
}
