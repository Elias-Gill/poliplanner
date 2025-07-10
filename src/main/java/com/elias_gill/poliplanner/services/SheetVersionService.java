package com.elias_gill.poliplanner.services;

import com.elias_gill.poliplanner.models.SheetVersion;
import com.elias_gill.poliplanner.repositories.SheetVersionRepository;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SheetVersionService {

    @Autowired private SheetVersionRepository repository;

    @Transactional
    public SheetVersion create(String fileName, String url) {
        return repository.save(new SheetVersion(fileName, url));
    }

    public SheetVersion findLatest() {
        return repository.findFirstByOrderByParsedAtDesc();
    }
}
