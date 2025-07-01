package tn.esprit.spring.services.universite;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.spring.dao.entities.Universite;
import tn.esprit.spring.dao.repositories.UniversiteRepository;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UniversiteService implements IUniversiteService {
    UniversiteRepository repo;

    @Override
    public Universite addOrUpdate(Universite u) {
        return repo.save(u);
    }

    @Override
    public List<Universite> findAll() {
        return repo.findAll();
    }

    @Override
    public Universite findById(long id) {
        Optional<Universite> universiteOptional = repo.findById(id);
        if (universiteOptional.isPresent()) {
            return universiteOptional.get();
        } else {
            // Handle the case when not found - could return null, throw a custom exception, etc.
            return null;
        }
    }

    @Override
    public void deleteById(long id) {
        repo.deleteById(id);
    }

    @Override
    public void delete(Universite u) {
        repo.delete(u);
    }

    @Override
    public Universite ajouterUniversiteEtSonFoyer(Universite u) {
        return repo.save(u);
    }
}
