package com.elias_gill.poliplanner.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.elias_gill.poliplanner.models.Career;
import com.elias_gill.poliplanner.repositories.CareerRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class CareerService {

    @Autowired
    private CareerRepository careerRepository;

    public Career findOrCreate(String name) {
        Career aux = careerRepository.findByNameIgnoreCase(name);
        // Crear si no existe
        if (aux == null) {
            return careerRepository.save(new Career(name));
        }
        return aux;
    }
}
