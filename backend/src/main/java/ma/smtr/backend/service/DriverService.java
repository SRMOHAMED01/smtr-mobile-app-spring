package ma.smtr.backend.service;

import lombok.RequiredArgsConstructor;
import ma.smtr.backend.dto.request.DriverRequest;
import ma.smtr.backend.entity.Driver;
import ma.smtr.backend.exception.ResourceNotFoundException;
import ma.smtr.backend.repository.DriverRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DriverService {

    private final DriverRepository driverRepository;

    public List<Driver> findAll() {
        return driverRepository.findAll();
    }

    public Driver findById(Long id) {
        return driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Chauffeur introuvable : id=" + id));
    }

    public Driver create(DriverRequest request) {
        Driver driver = new Driver();
        driver.setNom(request.nom());
        driver.setPrenom(request.prenom());
        driver.setTelephone(request.telephone());
        return driverRepository.save(driver);
    }

    public Driver update(Long id, DriverRequest request) {
        Driver driver = findById(id);
        driver.setNom(request.nom());
        driver.setPrenom(request.prenom());
        driver.setTelephone(request.telephone());
        return driverRepository.save(driver);
    }

    public void delete(Long id) {
        driverRepository.delete(findById(id));
    }
}
