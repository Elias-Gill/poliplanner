package com.elias_gill.poliplanner.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.elias_gill.poliplanner.models.SheetVersion;
import com.elias_gill.poliplanner.repositories.SheetVersionRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class SheetVersionService {

    @Autowired
    private SheetVersionRepository repository;

    public SheetVersion create(String fileName, String url) {
        return repository.save(new SheetVersion(fileName, url));
    }

    public SheetVersion findLatest() {
        return repository.findFirstByOrderByParsedAtDesc();
    }
}
