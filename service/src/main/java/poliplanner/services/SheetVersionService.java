package poliplanner.services;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import poliplanner.models.SheetVersion;
import poliplanner.repositories.SheetVersionRepository;

@Service
@RequiredArgsConstructor
public class SheetVersionService {
    private final SheetVersionRepository repository;

    public SheetVersion findLatest() {
        return repository.findFirstByOrderByParsedAtDescIdDesc();
    }
}
