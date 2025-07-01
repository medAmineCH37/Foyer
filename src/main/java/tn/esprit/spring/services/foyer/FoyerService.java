package tn.esprit.spring.services.foyer;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.spring.dao.entities.*;
import tn.esprit.spring.dao.repositories.BlocRepository;
import tn.esprit.spring.dao.repositories.FoyerRepository;
import tn.esprit.spring.dao.repositories.UniversiteRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class FoyerService implements IFoyerService {
    private static final String UNIVERSITE_NOT_FOUND_MSG = "Universite not found with ID: ";
    private final FoyerRepository foyerRepository;
    FoyerRepository repo;
    UniversiteRepository universiteRepository;
    BlocRepository blocRepository;

    @Override
    public Foyer addOrUpdate(Foyer f) {
        return repo.save(f);
    }

    @Override
    public List<Foyer> findAll() {
        return repo.findAll();
    }

    @Override
    public Foyer findById(long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Foyer not found with id: " + id));
    }

    @Override
    public void deleteById(long id) {
        repo.deleteById(id);
    }

    @Override
    public void delete(Foyer f) {
        repo.delete(f);
    }

    @Override
    public Universite affecterFoyerAUniversite(long idFoyer, String nomUniversite) {
        Foyer f = findById(idFoyer); // Child
        var u = universiteRepository.findByNomUniversite(nomUniversite); // Parent
        // On affecte le child au parent
        u.setFoyer(f);
        return universiteRepository.save(u);
    }

    @Override
    public Foyer ajouterFoyerEtAffecterAUniversite(Foyer foyer, long idUniversite) {
        // Validate input
        if (foyer == null) {
            throw new IllegalArgumentException("Foyer cannot be null");
        }

        // Save foyer first to generate ID
        var savedFoyer = foyerRepository.save(foyer);

        // Get universite with proper error handling
        var universite = universiteRepository.findById(idUniversite)
                .orElseThrow(() -> new EntityNotFoundException(UNIVERSITE_NOT_FOUND_MSG + idUniversite));

        // Process blocs if they exist
        List<Bloc> blocs = foyer.getBlocs();
        if (blocs != null && !blocs.isEmpty()) {
            for (Bloc bloc : blocs) {
                bloc.setFoyer(savedFoyer);
                blocRepository.save(bloc);
            }
        }

        // Set bidirectional relationship
        universite.setFoyer(savedFoyer);
        savedFoyer.setUniversite(universite);

        // Save and return
        return foyerRepository.save(savedFoyer);
    }

    @Override
    public Foyer ajoutFoyerEtBlocs(Foyer foyer) {
        List<Bloc> blocs = foyer.getBlocs();
        foyer = repo.save(foyer);
        for (Bloc b : blocs) {
            b.setFoyer(foyer);
            blocRepository.save(b);
        }
        return foyer;
    }

    @Override
    public Universite affecterFoyerAUniversite(long idF, long idU) {
        // Safe retrieval with proper error handling
        Universite u = universiteRepository.findById(idU)
                .orElseThrow(() -> new EntityNotFoundException(UNIVERSITE_NOT_FOUND_MSG + idU));

        // Safe retrieval with proper error handling
        Foyer f = foyerRepository.findById(idF)
                .orElseThrow(() -> new EntityNotFoundException("Foyer not found with ID: " + idF));

        u.setFoyer(f);
        return universiteRepository.save(u);
    }

    @Override
    public Universite desaffecterFoyerAUniversite(long idUniversite) {
        // Safe retrieval with proper error handling
        Universite u = universiteRepository.findById(idUniversite)
                .orElseThrow(() -> new EntityNotFoundException(UNIVERSITE_NOT_FOUND_MSG + idUniversite));

        u.setFoyer(null);
        return universiteRepository.save(u);
    }


}
