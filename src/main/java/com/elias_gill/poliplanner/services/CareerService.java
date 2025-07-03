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

    @Autowired private CareerRepository careerRepository;

    public Career find(String name) {
        return careerRepository.findByNameIgnoreCase(name);
    }

    public List<Career> findAll() {
        // FIX: ver que no se esten repitiendo y de otros horarios viejos y eso
        return careerRepository.findAll();
    }

    public Career create(String name, SheetVersion version) {
        Career aux = new Career(name);
        aux.setVersion(version);
        return careerRepository.save(aux);
    }
}
