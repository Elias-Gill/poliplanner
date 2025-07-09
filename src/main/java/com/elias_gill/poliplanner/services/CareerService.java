package com.elias_gill.poliplanner.services;

import com.elias_gill.poliplanner.models.Career;
import com.elias_gill.poliplanner.models.SheetVersion;
import com.elias_gill.poliplanner.repositories.CareerRepository;

import jakarta.transaction.Transactional;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class CareerService {
    @Autowired
    private CareerRepository careerRepository;
    @Autowired
    private SheetVersionService sheetVersionService;

    public Career findByName(String name) {
        return careerRepository.findByNameIgnoreCase(name);
    }

    public List<Career> findCareers() {
        // FIX: posibles try catch and errors
        SheetVersion version = sheetVersionService.findLatest();
        return careerRepository.findByVersion(version);
    }

    public Career create(String name, SheetVersion version) {
        Career aux = new Career(name);
        aux.setVersion(version);
        return careerRepository.save(aux);
    }
}
