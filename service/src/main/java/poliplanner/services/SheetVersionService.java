package poliplanner.services;

import org.springframework.stereotype.Service;

import poliplanner.models.SheetVersion;
import poliplanner.repositories.SheetVersionRepository;

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
        return repository.findFirstByOrderByParsedAtDescIdDesc();
    }
}
