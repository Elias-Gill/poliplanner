package poliplanner.services;

import poliplanner.exception.BadArgumentsException;
import poliplanner.exception.InternalServerErrorException;
import poliplanner.models.Career;
import poliplanner.models.SheetVersion;
import poliplanner.repositories.CareerRepository;

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
