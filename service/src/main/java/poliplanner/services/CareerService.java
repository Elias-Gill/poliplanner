package com.elias_gill.poliplanner.services;

import com.elias_gill.poliplanner.exception.BadArgumentsException;
import com.elias_gill.poliplanner.exception.InternalServerErrorException;
import com.elias_gill.poliplanner.models.Career;
import com.elias_gill.poliplanner.models.SheetVersion;
import com.elias_gill.poliplanner.repositories.CareerRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CareerService {
    private final CareerRepository careerRepository;
    private final SheetVersionService sheetVersionService;

    public Career findByName(String name) {
        return careerRepository.findByNameIgnoreCase(name);
    }

    public List<Career> findCareers() throws InternalServerErrorException {
        SheetVersion version = sheetVersionService.findLatest();
        if (version == null) {
            throw new InternalServerErrorException("No existen versiones de excel parseada");
        }
        return careerRepository.findByVersion(version);
    }

    @Transactional
    public Career create(String name, SheetVersion version) throws InternalServerErrorException, BadArgumentsException {
        if (name == null || name.isEmpty() || name.isBlank()) {
            throw new BadArgumentsException("No se proporciono nombre de carrera");
        }

        if (version == null) {
            throw new BadArgumentsException("No se proporciono la version de plantilla excel");
        }

        Career aux = new Career(name);
        aux.setVersion(version);

        return careerRepository.save(aux);
    }
}
